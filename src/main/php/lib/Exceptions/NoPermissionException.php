<?php

namespace Kamicloud\StubApi\Exceptions;

class NoPermissionException extends BaseException
{
    public function __construct($message = null)
    {
        parent::__construct($message, -200);
    }

}
