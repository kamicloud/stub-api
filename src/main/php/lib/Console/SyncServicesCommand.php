<?php

namespace YetAnotherGenerator\Console;

use Illuminate\Console\Command;
use PhpParser\Node;
use PhpParser\Node\Stmt\ClassMethod;
use PhpParser\Node\Stmt\Namespace_;
use PhpParser\Node\Stmt\ClassLike;
use PhpParser\NodeVisitor\FindingVisitor;
use PhpParser\ParserFactory;
use Exception;

class SyncServicesCommand extends Command
{
    protected $signature = 'sync:services';

    protected $description = 'Command description';

    public function handle()
    {
        $controllersPath = app_path('Generated/Controllers');

        if (!file_exists(app_path('Http/Services'))) {
            mkdir(app_path('Http/Services'));
        }

        foreach (scandir($controllersPath) as $version) {
            if (in_array($version, ['.', '..'])) {
                continue;
            }
            foreach (scandir("$controllersPath/$version") as $controllerName) {
                if (in_array($controllerName, ['.', '..'])) {
                    continue;
                }
                if (!file_exists(app_path("Http/Services/$version"))) {
                    mkdir(app_path("Http/Services/$version"));
                }
                $controllerPath = "$controllersPath/$version/$controllerName";
                $serviceName = str_replace('Controller', 'Service', $controllerName);
                $servicePath = app_path("Http/Services/$version/$serviceName");

                $this->checkServiceExists($servicePath, $version, str_replace('.php', '', $serviceName));

                // 解析需测试文件
                $controllerActions = $this->getActionsFromFile($controllerPath);
                $serviceActions = $this->getActionsFromFile($servicePath);

                $diffActions = array_diff($controllerActions, $serviceActions);

                foreach ($diffActions as $actionName) {
                    $upperActionName = strtoupper($actionName[0]) . substr($actionName, 1);
                    $messageName = "{$upperActionName}Message";
                    $controllerRealName = str_replace('Controller.php', '', $controllerName);
                    $body = join("\n", [
                        "//use App\\Generated\\{$version}\\Messages\\{$controllerRealName}\\$messageName;",
                        "//public static function $actionName($messageName \$message)",
                        "//{",
                        "//}",
                        ""
                    ]);
                    file_put_contents($servicePath, $body, FILE_APPEND);
                }
            }
        }
    }

    protected function getActionsFromFile($path)
    {
        $code = file_get_contents($path);
        $parser = (new ParserFactory())->create(ParserFactory::PREFER_PHP7);
        $ast = $parser->parse($code);
        if (!count($ast)) {
            throw new Exception('未发现php代码');
        }
        $classMethods = $this->parsePHPSegment($ast);

        return array_map(function (ClassMethod $classMethod) {
            return $classMethod->name->name;
        }, $classMethods);
    }

    protected function parsePHPSegment($segment)
    {
        $foundNodes = [];
        if (is_array($segment)) {
            $segments = $segment;
            foreach ($segments as $segment) {
                $foundNodes = array_merge($foundNodes, $this->parsePHPSegment($segment));
            }
        } elseif ($segment instanceof Node) {
            $finder = new FindingVisitor(function (Node $node) use (&$foundNodes) {
                if ($node instanceof ClassMethod) {
                    return true;
                } elseif ($node instanceof Namespace_ || $node instanceof ClassLike) {
                    $foundNodes = array_merge($foundNodes, $this->parsePHPSegment($node->stmts));
                }
                return false;
            });
            $finder->beforeTraverse([]);
            $finder->enterNode($segment);
            $foundNodes = array_merge($foundNodes, $finder->getFoundNodes() ?: []);
        }

        return $foundNodes;
    }

    protected function checkServiceExists($path, $version, $serviceName)
    {
        if (!file_exists($path)) {
            file_put_contents($path, <<<FILE
<?php

namespace App\Http\Services\\$version;

class {$serviceName}
{
}

FILE
            );
        }
    }
}

