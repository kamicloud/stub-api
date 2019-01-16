<?php

namespace YetAnotherGenerator;

class BaseEnum
{
    const _MAP = [];

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

        $map = $map + array_combine($map, $map);

        return $map[$value] ?? null;
    }
}
