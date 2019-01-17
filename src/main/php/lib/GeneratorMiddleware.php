<?php

namespace YetAnotherGenerator;

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
        if (config('app.debug') === true) {
            if ($request->input('__test_mode', false)) {

                config([
                    'app.env' => 'testing',
                ]);

                DB::beginTransaction();

                $response = $next($request);

                /** @var JsonResponse $response */
                $content = $response->getContent();

                $content = json_encode(json_decode($content), JSON_PRETTY_PRINT);

                $response->setContent($content);

                DB::rollBack();

                return $response;
            }
        }
        return $next($request);
    }
}
