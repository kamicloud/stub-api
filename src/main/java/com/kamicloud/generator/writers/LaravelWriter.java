package com.kamicloud.generator.writers;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.common.MultiLinesCombiner;
import com.kamicloud.generator.writers.components.php.*;
import definitions.annotations.*;
import definitions.annotations.Optional;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LaravelWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private String version;

    private File outputDir;
    private File generatedDir;
    private File routePath;

    public LaravelWriter() {
        String laravelPath = Objects.requireNonNull(env.getProperty("generator.laravel-path"));
        outputDir = new File(laravelPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        generatedDir = new File(outputDir.getAbsolutePath() + "/app/Generated");
        routePath = new File(outputDir.getAbsolutePath() + "/routes/generated_routes.php");
    }

    @Override
    public void update(OutputStub output) {
        FileUtil.deleteAllFilesOfDir(generatedDir);
        output.getTemplates().forEach((version, templateStub) -> {
            this.version = version;

            try {
                ClassCombiner.setNamespacePathTransformer(this);
                writeModels(templateStub);
                writeHttp(templateStub);
                writeEnums(templateStub);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        try {
            writeErrors(output);
            writeRoute(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeModels(TemplateStub o) {
        o.getModels().forEach((modelName, modelStub) -> {
            try {
                ClassCombiner modelClassCombiner = new ClassCombiner(
                    "App\\Generated\\" + version + "\\Models\\" + modelName + "Model",
                    "YetAnotherGenerator\\DTOs\\DTO"
                );

                modelClassCombiner.addTrait("YetAnotherGenerator\\Concerns\\ValueHelper");

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
     * <p>
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
                            "YetAnotherGenerator\\Http\\Messages\\Message"
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
                        if (action.getAnnotations().containsKey(Transactional.name)) {
                            actionClassMethodCombiner.wrapBody(
                                "return DB::transaction(function () use ($request) {",
                                "});"
                            );
                        }

                        messageClassCombiner.addTrait("YetAnotherGenerator\\Concerns\\ValueHelper");
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
                    "App\\Generated\\" + version + "\\Enums\\" + enumStub.getName(),
                    "YetAnotherGenerator\\BOs\\Enum"
                );

                ClassConstantCombiner mapConstant = new ClassConstantCombiner(
                    "_MAP",
                    EnumStub.EnumStubItemType.EXPRESSION,
                    "public"
                );
                mapConstant.addLine("[");
                enumStub.getItems().forEach((key, value) -> {
                    String valueName = value.getName();
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

    private void writeErrors(OutputStub o) throws Exception {
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
                    "YetAnotherGenerator\\Exceptions\\BaseException"
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

    private void writeRoute(OutputStub o) throws IOException {
        PHPFileCombiner fileCombiner = new PHPFileCombiner();

        fileCombiner.setFileName(routePath.getAbsolutePath());

        o.getTemplates().forEach((version, templateStub) -> {
            templateStub.getControllers().forEach(controller -> controller.getActions().forEach((actionName, action) -> {
                String method = "post";
                String middlewarePart = "";
                if (action.hasAnnotation(Middleware.name)) {
                    AnnotationStub x = action.getAnnotations().get(Middleware.name);
                    middlewarePart = "->middleware(['" + String.join("', '", (String[]) x.getValues().get("value")) + "'])";
                }
                fileCombiner.addBlock(new MultiLinesCombiner(
                    "Route::" + method + "('" + action.getUri() + "', '" + version + "\\" + controller.getName() + "Controller@" + actionName + "')" + middlewarePart + ";"
                ));
            }));
        });

        fileCombiner.toFile();
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
     * @param parameters    参数
     * @param classCombiner 目标类
     */
    private void writeGetAttributeMapMethod(String methodName, HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        classCombiner.addUse("YetAnotherGenerator\\Utils\\Constants");
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, methodName);

        parameters.forEach((parameterName, parameterStub) -> {
            String typeName = parameterStub.getType();
            String typeModelName = getModelNameFromType(typeName);
            ArrayList<String> ruleList = new ArrayList<>();
            ArrayList<String> types = new ArrayList<>();
            boolean isArray = parameterStub.isArray();
            boolean isModel = parameterStub.isModel();
            boolean isEnum = parameterStub.isEnum();
            if (parameterStub.hasAnnotation(Optional.name)) {
                types.add("Constants::IS_OPTIONAL");
                ruleList.add("nullable");
            }
            if (parameterStub.hasAnnotation(Mutable.name)) {
                types.add("Constants::IS_MUTABLE");
            }
            if (isArray) {
                types.add("Constants::IS_ARRAY");
            }
            if (isModel) {
                classCombiner.addUse("App\\Generated\\" + version + "\\Models\\" + typeModelName + "Model");
                typeModelName = typeModelName + "Model::class";
                types.add("Constants::IS_MODEL");
            } else if (isEnum) {
                classCombiner.addUse("App\\Generated\\" + version + "\\Enums\\" + typeModelName);
                typeModelName = typeModelName + "::class";
                types.add("Constants::IS_ENUM");
            } else if (typeModelName.equals("Date")) {
                typeModelName = "'" + typeName + "'";
            } else {
                if (typeModelName.equals("float")) {
                    typeModelName = "numeric";
                }
                // 参数校验
                ruleList.add("bail");

                ruleList.add(typeModelName);
                typeModelName = "'" + String.join("|", ruleList) + "'";
            }
            ArrayList<String> params = new ArrayList<>(Arrays.asList(
                "'" + parameterName + "'",
                "'" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parameterName) + "'", // DBField
                typeModelName,
                types.isEmpty() ? "null" : String.join(" | ", types)
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
        } else if (type.startsWith("Enums.")) {
            typeName = typeName + "Enum";
        }
        return typeName;
    }
}
