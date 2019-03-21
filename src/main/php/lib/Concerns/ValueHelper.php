<?php

namespace YetAnotherGenerator\Concerns;

use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Validator;

use Throwable;
use YetAnotherGenerator\BOs\Enum;
use YetAnotherGenerator\DTOs\DTO;
use YetAnotherGenerator\Exceptions\InvalidParameterException;
use YetAnotherGenerator\Utils\Constants;

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
            [$field, $dbField, $rule, $type] = $attribute;

            $isModel = $type & Constants::IS_MODEL;
            $isArray = $type & Constants::IS_ARRAY;
            $isEnum = $type & Constants::IS_ENUM;

            $value = $values[$field] ?? null;

            if ($isArray || $isModel) {
                if (is_string($value)) {
                    $value = json_decode($value, true);
                }
            }

            if ($isArray) {
                if (is_array($value)) {
                    $this->$field = array_map(function ($value) use ($rule, $isModel, $isEnum) {
                        return $this->fromOne($value, $rule, $isModel, $isEnum);
                    }, $value);
                }
            } else {
                $this->$field = $this->fromOne($value, $rule, $isModel, $isEnum);
            }
        }
    }

    public function fromOne($value, $type, $isModel, $isEnum)
    {
        if (is_null($value)) {
            return null;
        }
        if ($isModel) {
            /** @var DTO $type */
            return $type::initFromModel($value);
        } elseif ($isEnum) {
            /** @var Enum $type */
            return $type::transform($value);
        } elseif ($type === 'Date') {
            return ValueHelper::convertDate($value);
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
        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $rule, $type] = $attribute;

            $isModel = $type & Constants::IS_MODEL;
            $isArray = $type & Constants::IS_ARRAY;
            $isEnum = $type & Constants::IS_ENUM;

            $value = $this->$field;

            if (!$isModel && !$isArray && !$isEnum && $type !== 'Date') {
                $data[$field] = $value;
                $rules[$field] = $rule;
            } else {
                $this->$field = $this->validateValue($value, $field, $rule, $type, "$location > $field");
            }
        }

        $validator = Validator::make($data, $rules);

        if ($validator->fails()) {
            $messages = $validator->messages()->messages();
            $messages = array_flatten($messages);

            $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);
            throw new $exception($location . "\n-----\n" . join("\n--\n", $messages));
        } else {
            foreach ($rules as $key => $rule) {
                $this->$key = $this->parseScalar($this->$key, $rule);
            }
        }
    }

    public function validateValue($value, $field, $rule, $type, $location)
    {
        $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);

        $isModel = $type & Constants::IS_MODEL;
        $isArray = $type & Constants::IS_ARRAY;
        $isOptional = $type & Constants::IS_OPTIONAL;
        $isEnum = $type & Constants::IS_ENUM;

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
                    throw new $exception("{$location} must be instance of {$field}");
                }

                $value->validateAttributes($value->getAttributeMap(), $location);
            } elseif ($isEnum) {
                /** @var Enum $rule */
                if ($rule::verify($value) === false) {
                    throw new $exception("{$location} should match enum");
                }
                $value = $rule::format($value);
            } elseif ($type !== 'Date') {
                $value = $this->parseScalar($value, $rule);
            }

        }

        return $value;
    }

    /**
     * 从标量数据类型中解析数据
     *
     * @param $value
     * @param $rule
     * @return int|string|null
     */
    public function parseScalar($value, $rule)
    {
        if (is_null($value)) {
            return null;
        }
        if (stripos($rule, 'int') !== false) {
            return (int) $value;
        } elseif (stripos($rule, 'bool')) {
            return (bool) $value;
        } else {
            return (string) $value;
        }
    }

    public static function convertDate($value, $format = 'Y-m-d H:i:s')
    {
        $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);
        if (is_null($value)) {
            return null;
        }

        try {
            if (is_numeric($value)) {
                return Carbon::createFromTimestamp($value);
            } else {
                return Carbon::createFromFormat($value, $format);
            }
        } catch (Throwable $e) {
            throw new $exception('cannot convert from date');
        }
    }

    public static function convertDates($values)
    {
        if (is_string($values)) {
            $values = json_decode($values, true);
        }

        if (is_null($values)) {
            return [];
        }

        return array_map(function ($value) {
            return self::convertDate($value);
        }, $values);
    }
}
