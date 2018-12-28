<?php

namespace YetAnotherGenerator;

use App\Generated\Exceptions\ServerInternalErrorException;
use Exception;
use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;

class Handler extends ExceptionHandler
{
    protected $dontReport = [
        BaseException::class
    ];

    public function render($request, Exception $exception)
    {
        if (!starts_with($request->getRequestUri(), '/api')) {
            return parent::render($request, $exception);
        }
        if ($exception instanceof BaseException) {
            return response()->json($exception->toResponse($request));
        }
        if (config('app.debug', false) !== true && !$request->input('__test_mode', false)) {
            return parent::render($request, $exception);
        } else {
            return self::render($request, new ServerInternalErrorException('Something went wrong.'));
        }
    }
}
