<?php

namespace YetAnotherGenerator;

use JsonSerializable;

class BaseModel implements JsonSerializable
{
    public static function fromJsonModel($model, $classname)
    {

    }

    public static function fromJsonModels($models, $classname)
    {

    }

    /**
     * Specify data which should be serialized to JSON.
     *
     * @link http://php.net/manual/en/jsonserializable.jsonserialize.php
     *
     * @return mixed data which can be serialized by <b>json_encode</b>,
     *               which is a value of any type other than a resource.
     *
     * @since 5.4.0
     */
    public function jsonSerialize()
    {
        // TODO: Implement jsonSerialize() method.
    }
}