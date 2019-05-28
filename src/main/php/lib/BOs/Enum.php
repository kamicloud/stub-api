<?php

namespace Kamicloud\StubApi\BOs;

abstract class Enum
{
    const _MAP = [];

    public static function verify($value)
    {
        $values = static::_MAP;

        return isset($values[$value]);
    }

    public static function format($value)
    {
        $values = static::_MAP;

        return array_flip($values)[$values[$value]];
    }

    public static function transform($value)
    {
        $map = static::_MAP;

        $map = array_flip($map);

        $map = $map + array_combine($map, $map);

        return $map[$value] ?? null;
    }
}
