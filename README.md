# API Generator

## 说明
本项目为laravel项目接口生成及自动测试工具。

本项目依赖 JDK

## 使用方法

接口部分使用说明
https://learnku.com/articles/25288

注解和对应生成代码详解
https://learnku.com/articles/26733

DEMO
https://github.com/Kamicloud/GeneratorDemoProject

#### 引入

````
composer require kamicloud/stub-api
````


执行 php artisan stub-api:install 将配置、模板及脚本自动配置在项目目录

执行bin目录引入的initGenerator，

修改laravel项目的一些代码的基类为generator基类

#### 目录结构
resources/generator目录下为模板目录包含以下子目录

templates 模板目录，包含TemplateList.java Template*.java Errors.java等

definitions 注解及定义目录，包含所有的注解及标量数据类型

config 工具配置目录，包含各个功能模块代码输出目录等配置

testcases 测试用例，执行生成会自动填充本目录

#### 使用方式

修改Template.java为所需要的格式，执行generate

修改及添加测试用例，执行autoTest

## 机制

initGenerator时引入gradle，加载依赖并编译generator

每次generate时，编译模板，替代原编译代码并执行generator
