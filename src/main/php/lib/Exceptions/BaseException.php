<?php

namespace YetAnotherGenerator\Exceptions;

use Exception;
use Illuminate\Contracts\Support\Responsable;

class BaseException extends Exception implements Responsable
{
    protected $status;
    protected $message;

    public function __construct($message, $status)
    {
        $this->status = $status;
        $this->message = $message;
        parent::__construct($message, 0, $this);
    }

    public function getStatus()
    {
        return $this->status;
    }

    public function toResponse($request)
    {
        return [
            config('generator.keys.status', 'status') => $this->getStatus(),
            config('generator.keys.message', 'message') => $this->getMessage(),
        ];
    }
}
