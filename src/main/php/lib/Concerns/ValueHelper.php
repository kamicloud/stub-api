<?php

namespace Kamicloud\StubApi\Concerns;

use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

use Throwable;
use Kamicloud\StubApi\BOs\Enum;
use Kamicloud\StubApi\DTOs\DTO;
use Kamicloud\StubApi\Exceptions\InvalidParameterException;
use Kamicloud\StubApi\Utils\Constants;

trait ValueHelper
{
    public function mutableAttributes($attributeMap)
    {
        $isTesting = config('app.env') === 'testing';

        $data = array_reduce($attributeMap, function ($c, $attribute) use ($isTesting) {
            [$field, $dbField, $rule, $type] = $attribute;

            $isMutable = $type & Constants::MUTABLE;

            // 测试模式会把非null的数据根据可测注解修改为通用数据
            $c[$field] = $isTesting && $isMutable && !is_null($this->{$field}) ? '*' : $this->{$field};

            return $c;
        }, []);

        return $data;
    }

    public function fromJson($values, $attributeMap)
    {
        if (is_string($values)) {
            $values = json_decode($values, true);
        }

        if (!$values) {
            return null;
        }

        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $rule, $type, $initParam] = $attribute;

            $isModel = $type & Constants::MODEL;
            $isArray = $type & Constants::ARRAY;

            $value = $values[$field] ?? null;

            if (is_string($value) && ($isArray || $isModel)) {
                $value = json_decode($value, true);
            }

            if ($isArray) {
                if (is_array($value)) {
                    $this->$field = array_map(function ($value) use ($rule, $type, $initParam) {
                        return $this->fromOne($value, $type, $rule, $initParam);
                    }, $value);
                }
            } else {
                $this->$field = $this->fromOne($value, $type, $rule, $initParam);
            }
        }
    }

    protected function fromOne($value, $type, $rule, $initParam)
    {
        if (is_null($value)) {
            return null;
        }
        if ($type & Constants::MODEL) {
            /** @var DTO $rule */
            return $rule::initFromModel($value);
        } elseif ($type & Constants::ENUM) {
            /** @var Enum $rule */
            return $rule::transform($value);
        } elseif ($type & Constants::DATE) {
            if (config('generator.request-date-timestamp')) {
                return date($initParam, $value);
            }
            return $value;
        } else {
            return $value;
        }
    }

    /**
     *
     *
     * @param array $attributeMap
     * @param string $location
     */
    public function validateAttributes(array $attributeMap, $location = 'root', $input = true)
    {
        $rules = [];
        $data = [];
        $types = [];
        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $rule, $type] = $attribute;

            $isModel = $type & Constants::MODEL;
            $isArray = $type & Constants::ARRAY;
            $isEnum = $type & Constants::ENUM;

            $value = $this->$field;

            if (!$isModel && !$isArray && !$isEnum) {
                $data[$field] = $value;
                $rules[$field] = $rule;
                $types[$field] = $type;
            } else {
                $this->$field = $this->validateValue($value, $field, $rule, $type, "$location > $field", $input);
            }
        }

        $validator = Validator::make($data, $rules);

        if ($validator->fails()) {
            $messages = $validator->messages()->toJson();
            $exception = config('generator.exceptions.invalid-parameter', InvalidParameterException::class);
            throw new $exception($location . $messages);
        } else {
            foreach ($types as $key => $type) {
                $this->$key = $this->forceScalarType($this->$key, $type, $input);
            }
        }
    }

    protected function validateValue($value, $field, $rule, $type, $location, $input)
    {
        $exception = config('generator.exceptions.invalid-parameter', InvalidParameterException::class);

        $isModel = $type & Constants::MODEL;
        $isArray = $type & Constants::ARRAY;
        $isOptional = $type & Constants::OPTIONAL;
        $isEnum = $type & Constants::ENUM;

        if (!$isOptional && is_null($value)) {
            throw new $exception("{$location} can not be null");
        }

        if ($isArray) {
            if (!is_array($value)) {
                if ($isOptional && $value === null) {
                    return null;
                }
                throw new $exception("{$location} should be array");
            }

            foreach ($value as $index => &$item) {
                $item = $this->validateValue($item, $field, $rule, $isModel | $isOptional | $isEnum, "$location(array) > $index", $input);
            }
            return $value;
        }

        if (!is_null($value)) {
            if ($isModel) {
                /** @var DTO $rule */
                if (!is_object($value) || !($value instanceof DTO) || get_class($value) !== $rule) {
                    throw new $exception("{$location} must be instance of {$rule}");
                }

                $value->validateAttributes($value->getAttributeMap(), $location, $input);
            } elseif ($isEnum) {
                /** @var Enum $rule */
                if ($rule::verify($value) === false) {
                    throw new $exception("{$location} should match enum");
                }
                $value = $rule::format($value);
            } else {
                $value = $this->forceScalarType($value, $type, $input);
            }

        }

        return $value;
    }

    /**
     * 从标量数据类型中解析数据
     *
     * @param $value
     * @param $type
     * @param $request
     * @return int|string|float|null
     */
    protected function forceScalarType($value, $type, $request = false)
    {
        if (is_null($value)) {
            return null;
        }
        if ($type & Constants::INTEGER) {
            return (int) $value;
        } elseif ($type & Constants::BOOLEAN) {
            return (bool) $value;
        } elseif ($type & Constants::FLOAT) {
            return (float) $value;
        } elseif ($type & Constants::DATE) {
            return $this->convertDate($value, $request);
        } elseif ($type & Constants::STRING) {
            return (string) $value;
        }

        return $value;
    }

    protected function convertDate($value, $request)
    {
        $exception = config('generator.exceptions.invalid-parameter', InvalidParameterException::class);
        if (is_null($value)) {
            return null;
        }

        try {
            if ($request) {
                return Carbon::createFromTimestamp(strtotime($value));
            }

            if (config('generator.response-date-timestamp')) {
                return strtotime($value);
            }
            return $value;
        } catch (Throwable $e) {
            throw new $exception('cannot convert from date');
        }
    }
}
