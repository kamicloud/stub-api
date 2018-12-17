<?php

namespace Kamicloud\YetAnotherGenerator;

use Exception;
use Illuminate\Contracts\Support\Responsable;

class BaseException extends Exception implements Responsable
{
    /**
     * Create an HTTP response that represents the object.
     *
     * @param  \Illuminate\Http\Request $request
     * @return array
     */
    public function toResponse($request)
    {
        return [
            'status' => $this->getCode(),
            'message' => $this->getMessage(),
        ];
    }
}