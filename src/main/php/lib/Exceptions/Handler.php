<?php

namespace Kamicloud\StubApi\Exceptions;

use Exception;
use Illuminate\Auth\Access\AuthorizationException;
use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;
use Illuminate\Foundation\Http\Exceptions\MaintenanceModeException;
use Illuminate\Support\Str;
use Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException;

class Handler extends ExceptionHandler
{
    protected $dontReport = [
        BaseException::class
    ];

    /**
     * @param \Illuminate\Http\Request $request
     * @param Exception $exception
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response|\Symfony\Component\HttpFoundation\Response
     */
    public function render($request, Exception $exception)
    {
        if (!Str::startsWith($request->getRequestUri(), 'api')) {
            return parent::render($request, $exception);
        }
        if ($exception instanceof MaintenanceModeException) {
            $exception = config('generator.exceptions.maintain-mode', MaintainModeException::class);
            $exception = new $exception('Maintaining');
        } elseif ($exception instanceof AuthorizationException) {
            $exception = config('generator.exceptions.no-permission', NoPermissionException::class);
            $exception = new $exception('No permission');
        } elseif ($exception instanceof UnauthorizedHttpException) {
            $exception = config('generator.exceptions.auth-failed', AuthFailedException::class);
            $exception = new $exception('Auth Failed');
        }
        if ($exception instanceof BaseException) {
            return response()->json($exception->toResponse($request));
        }
        if (config('app.debug', false) !== true && !$request->input('__test_mode', false)) {
            return parent::render($request, $exception);
        } else {
            $exceptionClass = config('generator.exceptions.server-internal-error', ServerInternalErrorException::class);
            return self::render($request, new $exceptionClass('Something went wrong.'));
        }
    }
}
