<?php

namespace YetAnotherGenerator;

use Closure;
use DB;

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

                DB::rollBack();

                return $response;
            }
        }
        return $next($request);
    }
}
