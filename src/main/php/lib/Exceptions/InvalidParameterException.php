<?php

namespace YetAnotherGenerator\Exceptions;

class InvalidParameterException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -2);
    }

}
