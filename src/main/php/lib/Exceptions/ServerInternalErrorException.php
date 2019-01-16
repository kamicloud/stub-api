<?php

namespace YetAnotherGenerator\Exceptions;

class ServerInternalErrorException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, 10001);
    }

}
