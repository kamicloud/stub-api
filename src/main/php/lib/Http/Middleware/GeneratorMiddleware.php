<?php

namespace Kamicloud\StubApi\Http\Middleware;

use Kamicloud\StubApi\Exceptions\ApiNotFoundException;
use Closure;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\DB;

class GeneratorMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $apiNotFoundException = config('generator.exceptions.api-not-found', ApiNotFoundException::class);
        $testMode = $request->input('__test_mode', false);
        if ($testMode) {
            if (config('app.debug') === true) {
                config([
                    'app.env' => 'testing',
                ]);

                DB::beginTransaction();

                $response = $next($request);

                if ($response instanceof JsonResponse) {
                    /** @var JsonResponse $response */
                    $content = $response->getContent();

                    $content = str_replace('\\n', '\\\\n', $content);

                    $content = json_encode(json_decode($content), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

                    $response->setContent($content);
                }

                DB::rollBack();

                return $response;
            } else {
                // 非调试模式时，使用超全局参数会抛出异常
                throw new $apiNotFoundException('Api not found!');
            }
        }
        return $next($request);
    }
}
