<?php

namespace YetAnotherGenerator\Exceptions;

class InvalidParameterException extends Base
{
    public function __construct($message = null)
    {
        parent::__construct($message, 10001);
    }

}
