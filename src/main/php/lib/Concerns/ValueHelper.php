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
            [$field, $dbField, $rule, $type] = $attribute;

            $isModel = $type & Constants::MODEL;
            $isArray = $type & Constants::ARRAY;

            $value = $values[$field] ?? null;

            if (is_string($value) && ($isArray || $isModel)) {
                $value = json_decode($value, true);
            }

            if ($isArray) {
                if (is_array($value)) {
                    $this->$field = array_map(function ($value) use ($rule, $type) {
                        return $this->fromOne($value, $type, $rule);
                    }, $value);
                }
            } else {
                $this->$field = $this->fromOne($value, $type, $rule);
            }
        }
    }

    protected function fromOne($value, $type, $rule)
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
        } else {
            return $value;
        }
    }

    /**
     * Validate attributes
     *
     * @param array $attributeMap
     * @param string $location
     * @param bool $input
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
            $isOptional = $type & Constants::OPTIONAL;

            $value = $this->$field;

            $data[$field] = $value;
            $types[$field] = $type;

            if ($isArray) {
                // 这里是为了检验是否是数组
                $rules[$field] = 'bail|array' . ($isOptional ? '|nullable' : '');

                $field = $field . '.*';
            }
            if ($isModel) {
                $rule = "bail|Models:{$rule}" . ($isOptional ? '|nullable' : '');
            } elseif ($isEnum) {
                $rule = "bail|Enums:{$rule}" . ($isOptional ? '|nullable' : '');
            }
            $rules[$field] = $rule;
        }

        $validator = Validator::make($data, $rules);

        if ($validator->fails()) {
            /** @var \Illuminate\Validation\Validator $validator */
            $message = $validator->messages()->first();
            $exception = config('generator.exceptions.invalid-parameter', InvalidParameterException::class);
            throw new $exception("location: $location > $message.");
        }
    }

    public function forceScalarTypes($attributeMap)
    {
        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $rule, $type] = $attribute;

            if (!$this->$field) {
                continue;
            }

            if ($this->$field instanceof Model) {
                $this->$field->forceDTOScalarTypes();
            } elseif ($type & Constants::ENUM) {
                /** @var Enum $rule */
                $this->$field = $rule::transform($this->$field);
            } else {
                $this->$field = $this->forceScalarType($this->$field, $type);
            }
        }
    }

    /**
     * 从标量数据类型中解析数据
     *
     * @param $value
     * @param $type
     * @param $request
     * @return int|string|float|null
     */
    protected function forceScalarType($value, $type)
    {
        if (is_null($value)) {
            return null;
        }
        if ($type & Constants::INTEGER) {
            return (int)$value;
        } elseif ($type & Constants::BOOLEAN) {
            return (bool)$value;
        } elseif ($type & Constants::FLOAT) {
            return (float)$value;
        } elseif ($type & Constants::DATE) {
            return $this->convertDate($value);
        } elseif ($type & Constants::STRING) {
            return (string)$value;
        }

        return $value;
    }

    protected function convertDate($value)
    {
        $exception = config('generator.exceptions.invalid-parameter', InvalidParameterException::class);
        if (is_null($value)) {
            return null;
        }

        try {
            if (config('generator.response-date-timestamp')) {
                return strtotime($value);
            }
            return $value;
        } catch (Throwable $e) {
            throw new $exception('cannot convert from date');
        }
    }
}
