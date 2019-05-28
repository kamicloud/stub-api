<?php

namespace Kamicloud\StubApi\Exceptions;

class ServerInternalErrorException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -1);
    }

}
