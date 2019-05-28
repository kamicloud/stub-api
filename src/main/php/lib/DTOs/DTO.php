<?php

namespace Kamicloud\StubApi\DTOs;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Collection;
use JsonSerializable;
use YetAnotherGenerator\Utils\Constants;
use YetAnotherGenerator\Concerns\ValueHelper;

abstract class DTO implements JsonSerializable
{
    use ValueHelper;

    public static function initFromModel($values)
    {
        if (is_string($values)) {
            $values = json_decode($values, true);
        }

        if (!$values) {
            return null;
        }

        $model = new static();

        $model->fromJson($values, $model->getAttributeMap());

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

    /**
     * @param Model|array|null $orm
     * @return null|static
     */
    public static function initFromEloquent($orm)
    {
        if ($orm === null) {
            return null;
        }

        $model = new static();

        if ($orm instanceof Model) {
            $values = $orm->attributesToArray() + $orm->getRelations();
        } else {
            // array
            $values = $orm;
        }

        $attributeMap = $model->getAttributeMap();

        foreach ($attributeMap as $attribute) {
            [$field, $dbField, $rule, $type] = $attribute;

            $isModel = $type & Constants::IS_MODEL;
            $isArray = $type & Constants::IS_ARRAY;

            $value = $values[$dbField] ?? null;

            if ($isModel) {
                /** @var DTO $rule */
                if ($isArray) {
                    $model->$field = $rule::initFromEloquents($value);
                } else {
                    $model->$field = $rule::initFromEloquent($value);
                }
            } else {
                $model->$field = $value;
            }
        }

        return $model;
    }

    /**
     * @param Collection|array|null $orms
     * @return array
     */
    public static function initFromEloquents($orms)
    {
        if ($orms === null) {
            return [];
        } elseif ($orms instanceof Collection) {
            $orms = $orms->all();
        }


        return array_map(function ($orm) {
            return static::initFromEloquent($orm);
        }, $orms);
    }

    abstract public function getAttributeMap();

    public function jsonSerialize()
    {
        $isTesting = config('app.env') === 'testing';

        return array_reduce($this->getAttributeMap(), function ($c, $attribute) use ($isTesting) {
            [$field, $dbField, $rule, $type] = $attribute;

            $isMutable = $type & Constants::IS_MUTABLE;

            // 测试模式会把非null的数据根据可测注解修改为通用数据
            $c[$field] = $isTesting && $isMutable && !is_null($this->{$field}) ? '*' : $this->{$field};

            return $c;
        }, []);
    }

    public function mock()
    {

    }

    public function toDBArray()
    {
        return array_reduce($this->getAttributeMap(), function ($c, $attribute) {
            [$field, $dbField] = $attribute;

            $c[$dbField] = $this->{$field};

            return $c;
        }, []);
    }
}
