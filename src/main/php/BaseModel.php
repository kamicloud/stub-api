<?php

namespace YetAnotherGenerator;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Collection;
use JsonSerializable;

abstract class BaseModel implements JsonSerializable
{

    public static function initFromModels(?array $values)
    {
        if ($values === null) {
            return [];
        }
        return array_map(function ($value) {
            return static::initFromModel($value);
        }, $values);
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

    abstract public static function initFromEloquent(?Model $orm);

    abstract public static function initFromModel($values);

    abstract public function getAttributeMap();

    public function jsonSerialize()
    {
        $isTesting = config('app.env') === 'testing';

        return array_reduce($this->getAttributeMap(), function ($c, $attributeMap) use ($isTesting) {
            [$field, $dbField, $isModel, $isArray, $type, $isMutable] = $attributeMap;

            $c[$field] = $isTesting && $isMutable ? '*' : $this->{$field};

            return $c;
        }, []);
    }

    public function mock()
    {

    }

}
