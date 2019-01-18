<?php

namespace YetAnotherGenerator\Console;

use Illuminate\Support\Str;
use Illuminate\Console\Command;
use Illuminate\Console\DetectsApplicationNamespace;

class InstallCommand extends Command
{
    use DetectsApplicationNamespace;

    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'generator:install';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Install all of the Generator resources';

    /**
     * Execute the console command.
     *
     * @return void
     */
    public function handle()
    {
//        $this->comment('Publishing Generator Service Provider...');
//        $this->callSilent('vendor:publish', ['--tag' => 'generator-provider']);

        $this->comment('Publishing Generator Template...');
        $this->callSilent('vendor:publish', ['--tag' => 'generator-resource']);

        $this->comment('Publishing Generator Configuration...');
        $this->callSilent('vendor:publish', ['--tag' => 'generator-config']);

//        $this->registerGeneratorServiceProvider();

        $this->info('Generator scaffolding installed successfully.');
    }

    /**
     * Register the Generator service provider in the application configuration file.
     *
     * @return void
     */
    protected function registerGeneratorServiceProvider()
    {
        $namespace = str_replace_last('\\', '', $this->getAppNamespace());

        $appConfig = file_get_contents(config_path('app.php'));

        if (Str::contains($appConfig, $namespace.'\\Providers\\GeneratorServiceProvider::class')) {
            return;
        }

        file_put_contents(config_path('app.php'), str_replace(
            "{$namespace}\\Providers\EventServiceProvider::class,".PHP_EOL,
            "{$namespace}\\Providers\EventServiceProvider::class,".PHP_EOL."        {$namespace}\Providers\GeneratorServiceProvider::class,".PHP_EOL,
            $appConfig
        ));

        file_put_contents(app_path('Providers/GeneratorServiceProvider.php'), str_replace(
            "namespace App\Providers;",
            "namespace {$namespace}\Providers;",
            file_get_contents(app_path('Providers/GeneratorServiceProvider.php'))
        ));
    }
}
