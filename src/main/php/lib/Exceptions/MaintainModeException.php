<?php

namespace Kamicloud\StubApi\Exceptions;

class MaintainModeException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -20);
    }

}
