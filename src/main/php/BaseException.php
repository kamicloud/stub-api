<?php

namespace YetAnotherGenerator;

use Exception;
use Illuminate\Contracts\Support\Responsable;
use Throwable;

class BaseException extends Exception implements Responsable
{
    protected $status;
    protected $message;

    public function __construct($message, $status)
    {
        $this->status = $status;
        $this->message = $message;
    }

    public function getStatus()
    {
        return $this->status;
    }

    public function toResponse($request)
    {
        return [
            'status' => $this->getStatus(),
            'message' => $this->getMessage(),
        ];
    }
}
