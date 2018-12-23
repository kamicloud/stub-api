<?php

namespace YetAnotherGenerator;

use App\Generated\Exceptions\InvalidParameterException;
use Validator;
use Throwable;

trait ValueHelper
{
    /**
     *
     *
     * @param array $attributeMap
     * @param string $location
     * @throws InvalidParameterException
     */
    public function validateAttributes(array $attributeMap, $location = 'root')
    {
        $rules = [];
        $data = [];
        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable, $isEnum] = $attribute;

            $value = $this->$field;

            if (!$isModel && !$isArray && !$isEnum && $type !== 'Date') {
                $data[$field] = $value;
                $rules[$field] = $type;
            } else {
                $this->validateValue($value, $field, $isModel, $isArray, $isEnum, $type, $isOptional, "$location > $field");
            }
        }

        $validator = Validator::make($data, $rules);

        if ($validator->fails()) {
            $messages = $validator->messages()->messages();
            $messages = array_flatten($messages);

            throw new InvalidParameterException($location . "\n-----\n" . join("\n--\n", $messages));
        }
    }

    public function validateValue($value, $field, $isModel, $isArray, $isEnum, $type, $isOptional, $location)
    {

        if (!$isOptional && is_null($value)) {
            throw new InvalidParameterException("{$location} can not be null");
        }

        if ($isArray) {
            if (!is_array($value)) {
                throw new InvalidParameterException("{$location} should be array");
            }

            foreach ($value as $index => $item) {
                $this->validateValue($item, $field, $isModel, false, $isEnum, $type, $isOptional, "$location(array) > $index");
            }
            return ;
        }

        if (!is_null($value)) {
            if ($isModel) {
                if (!is_object($value) || !($value instanceof BaseModel) || get_class($value) !== $type) {
                    throw new InvalidParameterException("{$location} must be instance of {$field}");
                }

                $value->validateAttributes($value->getAttributeMap(), $location);
            } elseif ($isEnum) {
                if ($type::verify($value) === false) {
                    throw new InvalidParameterException("{$location} should match enum");
                }
            } elseif ($type === 'Date') {
                // 无需校验
            } else {
            }

        }

    }

    /**
     * 从标量数据类型中解析数据
     *
     * @param $value
     * @param $type
     * @return int|string|null
     */
    public function parseScalar($value, $type)
    {
        if (is_null($value)) {
            return null;
        }
        if (stripos($type, 'int') !== false) {
            return (int) $value;
        } else {
            return (string) $value;
        }
    }

    public static function convertDate($value, $format = 'Y-m-d H:i:s')
    {
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
            throw new InvalidParameterException('cannot convert from date');
        }
    }

    public static function convertDates($values, $format = 'Y-m-d H:i:s')
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
