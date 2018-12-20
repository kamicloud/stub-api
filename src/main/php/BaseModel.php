<?php

namespace YetAnotherGenerator;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Collection;
use JsonSerializable;

abstract class BaseModel implements JsonSerializable
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

    abstract public static function initFromEloquent(?Model $orm);

    public static function initFromEloquents(?Collection $orms)
    {
        if ($orms === null) {
            return [];
        }
        return $orms->map(function ($orm) {
            return static::initFromEloquent($orm);
        })->all();
    }

    public function jsonSerialize()
    {
        foreach ($this->getAttributeMap() as $attributeMap) {
            [$field] = $attributeMap;

        }
        return array_reduce($this->getAttributeMap(), function ($c, $attributeMap) {
            [$field] = $attributeMap;

            $c[$field] = $this->{$field};

            return $c;
        }, []);
    }

    public function mock()
    {

    }

    abstract public function getAttributeMap();
}
