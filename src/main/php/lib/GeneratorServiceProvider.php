<?php

namespace Kamicloud\StubApi;

use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Facades\Validator;
use Kamicloud\StubApi\BOs\Enum;
use Kamicloud\StubApi\DTOs\DTO;

class GeneratorServiceProvider extends ServiceProvider
{
    public function boot()
    {
        $this->registerPublishing();

        Validator::extend('Models', function ($key, $value, $classes) {
            $class = $classes[0];
            if ($value === null) {
                return true;
            }
            if (!($value instanceof $class)) {
                return false;
            }

            /** @var DTO $value */
            $value->validateAttributes($value->getAttributeMap(), $key);
            return true;
        });
        Validator::extend('Enums', function ($key, $value, $classes) {
            $class = $classes[0];

            /** @var Enum $class */
            return $value === null || $class::verify($value);
        });
    }

    /**
     * Register the package routes.
     *
     * @return void
     */
//    private function registerRoutes()
//    {
//        Route::group($this->routeConfiguration(), function () {
//            $this->loadRoutesFrom(__DIR__.'/Http/routes.php');
//        });
//    }

    /**
     * Register the package's publishable resources.
     *
     * @return void
     */
    private function registerPublishing()
    {
        if ($this->app->runningInConsole()) {
            $this->publishes([
                __DIR__ . '/../../resources/config/application-prod.yml' => resource_path('generator/config/application.yml'),
                __DIR__ . '/../../resources/stubs' => resource_path('generator/stubs'),
                __DIR__ . '/../../java/definitions' => resource_path('generator/definitions'),
                __DIR__ . '/../../java/templates' => resource_path('generator/templates'),
            ], 'generator-resource');

            $this->publishes([
                __DIR__ . '/../config/generator.php' => config_path('generator.php'),
            ], 'generator-config');

            $this->publishes([
                __DIR__ . '/../bin' => base_path('bin'),
            ], 'generator-bin');


            if (!file_exists(storage_path('generator'))) {
                mkdir(storage_path('generator'));
            }
            file_put_contents(storage_path('generator/.gitignore'), "*\n!.gitignore\n");
        }
    }

    public function register()
    {
        $this->mergeConfigFrom(
            __DIR__ . '/../config/generator.php', 'generator'
        );

        $this->commands([
            Console\InstallCommand::class,
            Console\SyncServicesCommand::class,
        ]);
    }
}
