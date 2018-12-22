<?php

namespace YetAnotherGenerator;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Collection;
use JsonSerializable;

abstract class BaseModel implements JsonSerializable
{
    public function validateModel(array $attributeMap)
    {
        $this->validate($this->getAttributeMap());
    }

    public static function initFromModel($values)
    {
        if (is_string($values)) {
            $values = json_decode($values, true);
        }

        if (!$values) {
            return null;
        }

        $model = new static();

        $attributeMap = $model->getAttributeMap();

        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable] = $attribute;

            $value = $values[$field] ?? null;

            if ($isModel) {
                if ($isArray) {
                    $model->$field = $type::initFromModels($value);
                } else {
                    $model->$field = $type::initFromModel($value);
                }
            } elseif ($type === 'Date') {
                if ($isArray) {
                    $model->$field = ValueHelper::convertDate($value);
                }
            } else {
                $model->$field = $value;
            }
        }

        return $model;
    }

    public static function initFromModels($values)
    {
        if (is_string($values)) {
            $values = json_decode($values, true);
        }

        if ($values === null) {
            return [];
        }

        return array_map(function ($value) {
            return static::initFromModel($value);
        }, $values);
    }

    public static function initFromEloquent(?Model $orm)
    {
        if ($orm === null) {
            return null;
        }

        $model = new static();

        $values = $orm->attributesToArray() + $orm->getRelations();

        $attributeMap = $model->getAttributeMap();

        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable] = $attribute;

            $value = $values[$field] ?? null;

            if ($isModel) {
                if ($isArray) {
                    $model->$field = $type::initFromEloquents($value);
                } else {
                    $model->$field = $type::initFromEloquent($value);
                }
            } else {
                $model->$field = $value;
            }
        }

        return $model;
    }

    public static function initFromEloquents(?Collection $orms)
    {
        if ($orms === null) {
            return [];
        }

        return $orms->map(function ($orm) {
            return static::initFromEloquent($orm);
        })->all();
    }

//    abstract public static function initFromEloquent(?Model $orm);

//    abstract public static function initFromModel($value);

    abstract public function getAttributeMap();

    public function jsonSerialize()
    {
        $isTesting = config('app.env') === 'testing';

        return array_reduce($this->getAttributeMap(), function ($c, $attributeMap) use ($isTesting) {
            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable] = $attribute;

            // 测试模式会把非null的数据根据可测注解修改为通用数据
            $c[$field] = $isTesting && $isMutable && !is_null($this->{$field}) ? '*' : $this->{$field};

            return $c;
        }, []);
    }

    public function mock()
    {

    }

}
