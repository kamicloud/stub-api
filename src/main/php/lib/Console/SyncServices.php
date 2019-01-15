<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use PhpParser\Builder\Method;
use PhpParser\Builder\Use_;
use PhpParser\Node;
use PhpParser\Node\Stmt\Class_;
use PhpParser\Node\Stmt\ClassMethod;
use PhpParser\Node\Stmt\Namespace_;
use PhpParser\ParserFactory;
use PhpParser\PrettyPrinter;
use Exception;

class SyncServices extends Command
{
    protected $signature = 'sync:services';

    protected $description = 'Command description';

    public function handle()
    {
        $controllersPath = app_path('Generated/Controllers');

        foreach (scandir($controllersPath) as $version) {
            if (in_array($version, ['.', '..'])) {
                continue;
            }
            foreach (scandir("$controllersPath/$version") as $controllerName) {
                if (in_array($controllerName, ['.', '..'])) {
                    continue;
                }
                $controllerPath = "$controllersPath/$version/$controllerName";

                // 解析需测试文件
                $namespaces = $this->parseFile($controllerPath);
                array_walk($namespaces, function ($namespace) use ($version) {
                    [$namespace, $classes] = $namespace;



                    // 解析文件中的类
                    foreach ($classes as $class) {
                        $className = $class->name->name;
                        $serviceClassName = str_replace('Controller', 'Service', $className);
                        $servicesPath = app_path("Http/Services/$version/$serviceClassName.php");

                        $classMethodNames = array_filter(array_map(function ($bodyPart) {
                            if ($bodyPart instanceof ClassMethod && $bodyPart->isPublic()) {
                                $methodName = $bodyPart->name->name;
                                $upperCamelMethodName = strtoupper($methodName[0]) . substr($methodName, 1);
                                return [
                                    'lower_camel_method_name' => $methodName,
                                    'upper_camel_method_name' => $upperCamelMethodName,
                                ];
                            }
                            return null;
                        }, $class->stmts));

                        if (file_exists($servicesPath)) {
                            // 已存在
                            [$testNamespace, $testClassMethodNames, $testClass] = $this->getExistsTest($servicesPath);
//                            dd($classMethodNames, array_column($classMethodNames, 'lower_camel_method_name'));
                            $todoMethodNames = array_diff_key(
                                array_combine(array_column($classMethodNames, 'lower_camel_method_name'), $classMethodNames),
                                array_flip($testClassMethodNames)
                            );
                        } else {
                            // 创建空的测试类
                            $testNamespace = $this->prepareTestFile('App\Http\Services');
                            $todoMethodNames = $classMethodNames;
                            $testClass = new Class_($serviceClassName);
//                            $testClass->extends = new Node\Name('TestCase');
                        }

                        $testNamespace->stmts = array_filter($testNamespace->stmts, function ($stmt) {
                            return !($stmt instanceof Class_);
                        });

                        $testClass->stmts = array_merge($testClass->stmts, array_map(function ($methodName) use ($version, $serviceClassName, $testNamespace) {
                            [
                                'lower_camel_method_name' => $methodName,
                                'upper_camel_method_name' => $upperCamelMethodName,
                            ] = $methodName;
                            $method = new Method($methodName);
                            $method->makePublic();
                            $param = new Node\Param(new Node\Expr\Variable('message'), null, "{$upperCamelMethodName}Message");
                            $method->addParam($param);

                            $testNamespace->stmts[] = (new Node\Stmt\Use_(["App\Generated\Messages\{$version}\{$upperCamelMethodName}Message"], Node\Stmt\Use_::TYPE_NORMAL));

                            return $method->getNode();
                        }, $todoMethodNames));

                        $testNamespace->stmts[] = $testClass;

                        $prettyPrinter = new PrettyPrinter\Standard;
                        dd($prettyPrinter->prettyPrintFile([$testNamespace]));
                        file_put_contents($servicesPath, $prettyPrinter->prettyPrintFile([$testNamespace]));
                    }
                });
            }
        }
    }

    /**
     * 解析已有的测试
     *
     * @param $testFilePath
     * @return array
     * @throws Exception
     */
    protected function getExistsTest($testFilePath)
    {
        $testClasses = $this->parseFile($testFilePath);
        if (count($testClasses) !== 1) {
            throw new Exception('测试文件需有且仅有一个PHP片段');
        }
        [$testNamespace, $testClasses] = $testClasses[0];
        if (count($testClasses) !== 1) {
            throw new Exception('测试文件需有且仅有一个类');
        }
        $testClass = $testClasses[0];

        $testClassMethodNames = array_filter(array_map(function ($bodyPart) {
            if ($bodyPart instanceof ClassMethod && $bodyPart->isPublic()) {
                return $bodyPart->name->name;
            }
            return null;
        }, $testClass->stmts));

        return [$testNamespace, $testClassMethodNames, $testClass];
    }

    protected function prepareTestFile($namespace)
    {
        $namespace = new \PhpParser\Builder\Namespace_($namespace);
//        $namespace->addStmt(new Use_('Tests\TestCase', Node\Stmt\Use_::TYPE_NORMAL));

        return $namespace->getNode();
    }

    protected function parseFile($path)
    {
        $code = file_get_contents($path);
        $parser = (new ParserFactory())->create(ParserFactory::PREFER_PHP7);
        $ast = $parser->parse($code);
        if (!count($ast)) {
            throw new Exception('未发现php代码');
        }
        $namespaces = $this->parsePHPSegments($ast);

        return $namespaces;
    }

    protected function parsePHPSegments($segments)
    {
        $segments = array_filter($segments, function ($segment) {
            return $segment instanceof Namespace_;
        });

        $segments = array_map(function (Namespace_ $segment) {
            return [$segment, $this->parseNamespace($segment)];
        }, $segments);

        return $segments;
    }

    protected function parseNamespace(Namespace_ $namespace)
    {
        $classes = array_values(array_filter($namespace->stmts, function ($class) {
            return $class instanceof Class_;
        }));

        return $classes;
    }
}

