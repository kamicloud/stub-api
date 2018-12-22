<?php

namespace YetAnotherGenerator;

use App\Generated\Exceptions\InvalidParameterException;
use Throwable;

trait ValueHelper
{
    public function validate(array $attributeMap)
    {
        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable] = $attribute;

            $value = $this->$field;

            if (!$isOptional && is_null($value)) {
                throw new InvalidParameterException("{$field} can not be null");
            }

            if (is_object($value) && $value instanceof BaseModel) {
                if (get_class($value) !== $type) {
                    throw new InvalidParameterException("{$field} must be instance of {$field}");
                }

                $value->validateModel($value->getAttributeMap());
            }
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
