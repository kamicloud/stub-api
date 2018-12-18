<?php

namespace YetAnotherGenerator;

use JsonSerializable;

class BaseModel implements JsonSerializable
{

    public static function fromJsonModel($model, $classname)
    {
//        if (is_string($model)) {
//            $model = json_decode($model, true);
//        }
//        if (empty($model)) {
//            return null;
//        }
//        foreach
    }

    public static function initFromEloquent($orm)
    {
        $model = new static;
        if ($orm instanceof Arrayable) {
            $values = $orm->toArray();
        }

        foreach ($model->getAttributeMap() as $attributeMap) {
            [$field, $dbField, $isModel, $isArray, $type] = $attributeMap;
            $value = data_get($values, $dbField);
            if ($type === 'Date' && !empty($value)) {
                $value = strtotime($value);
            } elseif ($isArray && is_array($value)) {
                $value = array_map('initFromEloquent', $value);
            }
            $model->{$field} = $value;
        }

        return $model;
    }

    public static function initFromEloquents(Collection $orms)
    {
        return $orms->map(function ($orm) {
            return self::initFromEloquent($orm);
        });
    }

    public function jsonSerialize()
    {
        return [
            ''
        ];
    }

    public function mock()
    {

    }
}