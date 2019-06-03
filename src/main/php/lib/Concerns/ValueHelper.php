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
                        return $this->fromOne($value, $type, $initParam);
                    }, $value);
                }
            } else {
                $this->$field = $this->fromOne($value, $type, $initParam);
            }
        }
    }

    protected function fromOne($value, $type, $initParam)
    {
        if (is_null($value)) {
            return null;
        }
        if ($type & Constants::MODEL) {
            /** @var DTO $type */
            return $type::initFromModel($value);
        } elseif ($type & Constants::ENUM) {
            /** @var Enum $type */
            return $type::transform($value);
        } elseif ($type & Constants::DATE) {
            return $this->convertDate($value, $initParam);
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
    public function validateAttributes(array $attributeMap, $location = 'root')
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

            if (!$isModel && !$isArray && !$isEnum && $rule !== 'Date') {
                $data[$field] = $value;
                $rules[$field] = $rule;
                $types[$field] = $type;
            } else {
                $this->$field = $this->validateValue($value, $field, $rule, $type, "$location > $field");
            }
        }

        $validator = Validator::make($data, $rules);

        if ($validator->fails()) {
            $messages = $validator->messages()->toJson();
            $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);
            throw new $exception($location . $messages);
        } else {
            foreach ($types as $key => $type) {
                $this->$key = $this->forceScalarType($this->$key, $type);
            }
        }
    }

    protected function validateValue($value, $field, $rule, $type, $location)
    {
        $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);

        $isModel = $type & Constants::MODEL;
        $isArray = $type & Constants::ARRAY;
        $isOptional = $type & Constants::OPTIONAL;
        $isEnum = $type & Constants::ENUM;

        if (!$isOptional && is_null($value)) {
            throw new $exception("{$location} can not be null");
        }

        if ($isArray) {
            if (!is_array($value)) {
                throw new $exception("{$location} should be array");
            }

            foreach ($value as $index => &$item) {
                $item = $this->validateValue($item, $field, $rule, $isModel | $isOptional | $isEnum, "$location(array) > $index");
            }
            return $value;
        }

        if (!is_null($value)) {
            if ($isModel) {
                /** @var DTO $rule */
                if (!is_object($value) || !($value instanceof DTO) || get_class($value) !== $rule) {
                    throw new $exception("{$location} must be instance of {$rule}");
                }

                $value->validateAttributes($value->getAttributeMap(), $location);
            } elseif ($isEnum) {
                /** @var Enum $rule */
                if ($rule::verify($value) === false) {
                    throw new $exception("{$location} should match enum");
                }
                $value = $rule::format($value);
            } else {
                $value = $this->forceScalarType($value, $type);
            }

        }

        return $value;
    }

    /**
     * 从标量数据类型中解析数据
     *
     * @param $value
     * @param $type
     * @return int|string|float|null
     */
    protected function forceScalarType($value, $type)
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
        } elseif ($type & Constants::STRING) {
            return (string) $value;
        }

        return $value;
    }

    protected function convertDate($value, $format = 'Y-m-d H:i:s')
    {
        $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);
        if (is_null($value)) {
            return null;
        }

        try {
            if (is_numeric($value)) {
                return Carbon::createFromTimestamp($value);
            } else {
                return Carbon::createFromFormat($format, $value);
            }
        } catch (Throwable $e) {
            throw new $exception('cannot convert from date');
        }
    }
}
