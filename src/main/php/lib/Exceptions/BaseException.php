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
            'status' => $this->getStatus(),
            'message' => $this->getMessage(),
        ];
    }
}
