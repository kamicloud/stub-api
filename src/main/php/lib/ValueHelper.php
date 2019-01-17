<?php

namespace YetAnotherGenerator;

use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Validator;
use Throwable;

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
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable, $isEnum] = $attribute;

            $value = $values[$field] ?? null;

            if ($isArray || $isModel) {
                if (is_string($value)) {
                    $value = json_decode($value, true);
                }
            }

            if ($isArray) {
                if (is_array($value)) {
                    $this->$field = array_map(function ($value) use ($type, $isModel, $isEnum) {
                        return $this->fromOne($value, $type, $isModel, $isEnum);
                    }, $value);
                }
            } else {
                $this->$field = $this->fromOne($value, $type, $isModel, $isEnum);
            }
        }
    }

    public function fromOne($value, $type, $isModel, $isEnum)
    {
        if ($isModel) {
            /** @var BaseModel $type */
            return $type::initFromModel($value);
        } elseif ($isEnum) {
            /** @var BaseEnum $type */
            return $type::transform($value);
        } elseif ($type === 'Date') {
            return ValueHelper::convertDate($value);
        } else {
            return $this->parseScalar($value, $type);
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

            $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);
            throw new $exception($location . "\n-----\n" . join("\n--\n", $messages));
        }
    }

    public function validateValue($value, $field, $isModel, $isArray, $isEnum, $type, $isOptional, $location)
    {
        $exception = config('generator.exceptions.invalid-parameter-exception', InvalidParameterException::class);

        if (!$isOptional && is_null($value)) {
            throw new $exception("{$location} can not be null");
        }

        if ($isArray) {
            if (!is_array($value)) {
                throw new $exception("{$location} should be array");
            }

            foreach ($value as $index => $item) {
                $this->validateValue($item, $field, $isModel, false, $isEnum, $type, $isOptional, "$location(array) > $index");
            }
            return ;
        }

        if (!is_null($value)) {
            if ($isModel) {
                /** @var BaseModel $type */
                if (!is_object($value) || !($value instanceof BaseModel) || get_class($value) !== $type) {
                    throw new $exception("{$location} must be instance of {$field}");
                }

                $value->validateAttributes($value->getAttributeMap(), $location);
            } elseif ($isEnum) {
                /** @var BaseEnum $type */
                if ($type::verify($value) === false) {
                    throw new $exception("{$location} should match enum");
                }
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
