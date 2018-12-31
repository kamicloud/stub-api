# API Generator

## 说明
本项目为laravel项目接口生成及自动测试工具，第一版基于JavaParser实现，代码简陋还有很多功能没有实现，后续会改为反射方式实现功能。

## 使用方法
1、添加git到composer.json

    "repositories": [
        {
            "type": "git",
            "url": "https://github.com/Ttdnts/APIGenerator.git"
        }
    ]
    

2、修改laravel项目的一些代码的基类为generator基类

3、修改application.yml路径为本地项目路径

4、run Generator

5、修改Template.java为所需要的格式