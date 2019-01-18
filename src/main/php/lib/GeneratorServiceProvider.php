<?php

namespace YetAnotherGenerator;

use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Facades\Route;

class GeneratorServiceProvider extends ServiceProvider
{
    public function boot()
    {
        $this->registerPublishing();
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
                __DIR__ . '/../../java/definitions' => resource_path('generator/definitions'),
                __DIR__ . '/../../java/templates' => resource_path('generator/templates'),
            ], 'generator-resource');

            $this->publishes([
                __DIR__ . '/../config/generator.php' => config_path('generator.php'),
            ], 'generator-config');
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
