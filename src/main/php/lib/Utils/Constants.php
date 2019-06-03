<?php

namespace Kamicloud\StubApi\Utils;

class Constants
{
    const OPTIONAL = 0b1;
    const ARRAY    = 0b10;
    const MUTABLE  = 0b100;
    const BOOLEAN  = 0b1000;
    const INTEGER  = 0b10000;
    const FLOAT    = 0b100000;
    const STRING   = 0b1000000;
    const DATE     = 0b10000000;
    const MODEL    = 0b10000000000;
    const ENUM     = 0b100000000000;
}
