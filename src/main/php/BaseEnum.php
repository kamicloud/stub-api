<?php

namespace YetAnotherGenerator;

class BaseEnum
{
    public static function verify($value)
    {
        $values = static::_MAP;

        if (!isset($values[$value])) {
            return false;
        }

        return true;
    }

    public static function transform($value)
    {
        $map = static::_MAP;

        $map = array_flip($map);
        $map = array_merge($map, array_combine($map, $map));

        return $map[$value] ?? null;
    }
}
