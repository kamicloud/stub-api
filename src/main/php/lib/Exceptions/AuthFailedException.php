<?php

namespace Kamicloud\StubApi\Exceptions;

class AuthFailedException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -100);
    }

}
