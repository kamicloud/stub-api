package com.kamicloud.generator.writers;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.annotations.Mutable;
import com.kamicloud.generator.annotations.Optional;
import com.kamicloud.generator.annotations.StringEnum;
import com.kamicloud.generator.annotations.Transactional;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.php.*;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LaravelWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private String version;

    private File outputDir;
    private File generatedDir;
    private File routePath;

    public LaravelWriter(Environment env) throws Exception {
        super(env);
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.laravel-path")));
        if (!outputDir.exists()) {
            throw new Exception("未找到laravel目录");
        }
        generatedDir = new File(outputDir.getAbsolutePath() + "/app/Generated");
        routePath = new File(outputDir.getAbsolutePath() + "/routes/generated_routes.php");
    }

    @Override
    public void update(Observable o, Object arg) {
        FileUtil.deleteAllFilesOfDir(generatedDir);
        ((OutputStub) o).getTemplates().forEach((version, templateStub) -> {
            this.version = version;

            try {
                ClassCombiner.setNamespacePathTransformer(this);
                writeModels(templateStub);
                writeHttp(templateStub);
                writeEnums(templateStub);
                writeRoute(templateStub);
                writeErrors(templateStub);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void writeModels(TemplateStub o) {
        o.getModels().forEach((modelName, modelStub) -> {
            try {
                ClassCombiner modelClassCombiner = new ClassCombiner(
                    "App\\Generated\\" + version + "\\Models\\" + modelName + "Model",
                    "YetAnotherGenerator\\BaseModel"
                );

                modelClassCombiner.addTrait("YetAnotherGenerator\\ValueHelper");

                HashMap<String, ParameterStub> parameters = modelStub.getParameters();

                writeParameterAttributes(parameters, modelClassCombiner);
                writeParameterGetters(parameters, modelClassCombiner);
                writeParameterSetters(parameters, modelClassCombiner);
                writeGetAttributeMapMethod("getAttributeMap", parameters, modelClassCombiner);

                modelClassCombiner.toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * HTTP层模板生成
     *
     * 1、不使用
     *
     * @param o 模板解析文件
     */
    private void writeHttp(TemplateStub o) {
        o.getControllers().forEach(controllerStub -> {
            try {
                ClassCombiner controllerClassCombiner = new ClassCombiner(
                        "App\\Generated\\Controllers\\" + version + "\\" + controllerStub.getName() + "Controller",
                        "App\\Http\\Controllers\\Controller"
                );

                controllerStub.getActions().forEach((actionName, action) -> {
                    try {
                        String requestClassName = "Illuminate\\Http\\Request";
                        String messageClassName = "App\\Generated\\" + version + "\\Messages\\" + controllerStub.getName() + "\\" + actionName + "Message";

                        ClassCombiner messageClassCombiner = new ClassCombiner(
                                messageClassName,
                                "YetAnotherGenerator\\BaseMessage"
                        );

                        String lowerCamelActionName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, action.getName());

                        controllerClassCombiner.addUse("App\\Http\\Services\\" + version + "\\" + controllerStub.getName() + "Service");
                        controllerClassCombiner.addUse("DB");

                        ClassMethodCombiner actionClassMethodCombiner = new ClassMethodCombiner(controllerClassCombiner, lowerCamelActionName);
                        new ClassMethodParameterCombiner(actionClassMethodCombiner, "request", requestClassName);

                        actionClassMethodCombiner.setBody(
                                "$message = new " + actionClassMethodCombiner.addUse(messageClassName) + "($request);",
                                "$message->validateInput();",
                                controllerStub.getName() + "Service::" + lowerCamelActionName + "($message);",
                                "$message->validateOutput();",
                                "return $message->getResponse();"
                        );
                        if (action.getAnnotations().contains(new AnnotationStub(Transactional.name))) {
                            actionClassMethodCombiner.wrapBody(
                                    "return DB::transaction(function () use ($request) {",
                                    "});"
                            );
                        }

                        messageClassCombiner.addTrait("YetAnotherGenerator\\ValueHelper");
                        // message
                        writeParameterGetters(action.getRequests(), messageClassCombiner);
                        writeParameterAttributes(action.getRequests(), messageClassCombiner);
                        writeParameterAttributes(action.getResponses(), messageClassCombiner);
                        writeGetAttributeMapMethod("requestRules", action.getRequests(), messageClassCombiner);
                        writeGetAttributeMapMethod("responseRules", action.getResponses(), messageClassCombiner);
                        ClassMethodCombiner setResponseMethod = new ClassMethodCombiner(messageClassCombiner, "setResponse");
                        writeMethodParameters(action.getResponses(), setResponseMethod);

                        messageClassCombiner.toFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                controllerClassCombiner.toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void writeEnums(TemplateStub o) {
        o.getEnums().forEach(enumStub -> {
            try {
                ClassCombiner enumClassCombiner = new ClassCombiner(
                        "App\\Generated\\" + version + "\\Enums\\" + enumStub.getName() + "Enum",
                        "YetAnotherGenerator\\BaseEnum"
                );

                ClassConstantCombiner mapConstant = new ClassConstantCombiner(
                        "_MAP",
                        EnumStub.EnumStubItemType.EXPRESSION,
                        "protected"
                );
                mapConstant.addLine("[");
                enumStub.getItems().forEach((key, value) -> {
                    String valueName =  value.getName();
                    EnumStub.EnumStubItemType valueType = value.getType();

                    if (enumStub.hasAnnotation(StringEnum.name)) {
                        valueType = EnumStub.EnumStubItemType.STRING;
                        valueName = key;
                    }
                    ClassConstantCombiner enumClassConstantCombiner = new ClassConstantCombiner(key, valueType);
                    enumClassConstantCombiner.addLine(valueName);
                    enumClassCombiner.addConstant(enumClassConstantCombiner);

                    mapConstant.addLine("    self::" + key + " => '" + key + "',");
                });
                mapConstant.addLine("]");
                enumClassCombiner.addConstant(mapConstant);

                enumClassCombiner.toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void writeErrors(TemplateStub o) throws Exception {
        ClassCombiner errorCodeClassCombiner = new ClassCombiner("App\\Generated\\Exceptions\\ErrorCode");
        o.getErrors().forEach(error -> {
            try {
                // error code
                ClassConstantCombiner constant = new ClassConstantCombiner(error.getName(), null);
                constant.addLine(error.getCode());
                errorCodeClassCombiner.addConstant(constant);
                String exceptionName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, error.getName());
                ClassCombiner exceptionClassCombiner = new ClassCombiner(
                        "App\\Generated\\Exceptions\\" + exceptionName + "Exception",
                        "YetAnotherGenerator\\BaseException"
                );

                ClassMethodCombiner constructMethodCombiner = new ClassMethodCombiner(exceptionClassCombiner, "__construct");
                new ClassMethodParameterCombiner(constructMethodCombiner, "message", null, "null");
                constructMethodCombiner.addBody("parent::__construct($message, ErrorCode::" + error.getName() + ");");

                exceptionClassCombiner.toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        errorCodeClassCombiner.toFile();
    }

    private void writeRoute(TemplateStub o) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(routePath);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        outputStreamWriter.write("<?php\n");

        o.getControllers().forEach(controller -> controller.getActions().forEach((actionName, action) -> {
            String method = "post";
            try {
                outputStreamWriter.write("Route::" + method + "('" + action.getUri() + "', '" + version + "\\" + controller.getName() + "Controller@" + actionName + "');\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        outputStreamWriter.close();
        fileOutputStream.close();
    }

    @Override
    public String namespaceToPath(String namespace) {
        return outputDir.getAbsolutePath() + "/" + namespace.replace("App\\", "app/").replace("\\", "/") + ".php";
    }

    @Override
    public String pathToNamespace(String path) {
        return null;
    }

    private void writeParameterAttributes(HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterName, parameterStub) -> new ClassAttributeCombiner(classCombiner, parameterStub.getName(), "protected"));
    }

    private void writeParameterGetters(HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterName, parameterStub) -> writeParameterGetter(parameterStub, classCombiner));
    }

    private void writeParameterGetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        if (parameterStub.getType().equals("boolean")) {
            parameterName = "is" + parameterName;
        } else {
            parameterName = "get" + parameterName;
        }
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, parameterName);
        classMethodCombiner.setBody("return $this->" + parameterStub.getName() + ";");
        String comment = parameterStub.getComment();
        if (comment != null) {
            classMethodCombiner.addComment(parameterStub.getComment());
        }
    }

    private void writeParameterSetters(HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterName, parameterStub) -> writeParameterSetter(parameterStub, classCombiner));
    }

    private void writeParameterSetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, "set" + parameterName);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
        new ClassMethodParameterCombiner(classMethodCombiner, parameterStub.getName());
    }

    private void writeMethodParameters(HashMap<String, ParameterStub> parameters, ClassMethodCombiner classMethodCombiner) {
        parameters.forEach((parameterName, parameterStub) -> writeMethodParameter(parameterStub, classMethodCombiner));
    }

    private void writeMethodParameter(ParameterStub parameterStub, ClassMethodCombiner classMethodCombiner) {
        new ClassMethodParameterCombiner(classMethodCombiner, parameterStub.getName());

        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
    }

    /**
     *
     * @param parameters 参数
     * @param classCombiner 目标类
     */
    private void writeGetAttributeMapMethod(String methodName, HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, methodName);

        parameters.forEach((parameterName, parameterStub) -> {
            String typeName = parameterStub.getType();
            String typeModelName = getModelNameFromType(typeName);
            ArrayList<String> ruleList = new ArrayList<>();
            boolean isArray = false;
            boolean isModel = false;
            boolean isEnum = false;
            if (typeName.endsWith("[]")) {
                isArray = true;
            }
            if (typeName.startsWith("Models")) {
                isModel = true;
                classCombiner.addUse("App\\Generated\\" + version + "\\Models\\" + typeModelName);
                typeModelName = typeModelName + "::class";
            } else if (typeName.startsWith("Enums")) {
                isEnum = true;
                classCombiner.addUse("App\\Generated\\" + version + "\\Enums\\" + typeModelName);
                typeModelName = typeModelName + "::class";
            } else if (typeModelName.equals("Date")) {
                typeModelName = "'" + typeName + "'";
            } else {
                // 参数校验
                ruleList.add("bail");
                if (!parameterStub.hasAnnotation(Optional.name)) {
                    ruleList.add("required");
                } else {
                    ruleList.add("nullable");
                }

                ruleList.add(typeModelName);
                typeModelName = "'" + String.join("|", ruleList) + "'";
            }
            ArrayList<String> params = new ArrayList<>(Arrays.asList(
                    "'" + parameterName + "'",
                    "'" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parameterName) + "'", // DBField
                    Boolean.toString(isModel),
                    Boolean.toString(isArray),
                    typeModelName,
                    Boolean.toString(parameterStub.hasAnnotation(Optional.name)),
                    Boolean.toString(parameterStub.hasAnnotation(Mutable.name)),
                    Boolean.toString(isEnum)
            ));
            classMethodCombiner.addBody("[" + String.join(", ", params) + "],");
        });
        classMethodCombiner.wrapBody("return [", "];");
    }

    private String getModelNameFromType(String type) {
        String typeName = type
                .replace("[]", "")
                .replace("Models.", "")
                .replace("Enums.", "");
        if (type.startsWith("Models.")) {
            typeName = typeName + "Model";
        } else if (type.startsWith("Enums."))  {
            typeName = typeName + "Enum";
        }
        return typeName;
    }
}
