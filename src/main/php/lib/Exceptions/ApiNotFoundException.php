<?php

namespace Kamicloud\StubApi\Exceptions;

class ApiNotFoundException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -11);
    }

}
