package com.kamicloud.generator.writers;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.common.MultiLinesCombiner;
import com.kamicloud.generator.writers.components.php.*;
import definitions.annotations.*;
import definitions.annotations.Optional;
import definitions.official.TypeSpec;
import definitions.types.Type;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LaravelWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private File outputDir;
    private File generatedDir;
    private File routePath;

    private String boFolder;
    private String serviceSuffix;
    private String serviceFolder;

    private String valueHelperNamespace;
    private String baseDTONamespace;
    private String baseMessageNamespace;
    private String baseEnumNamespace;
    private String baseExceptionNamespace;

    private HashMap<TypeSpec, String> returnTypeMap = new HashMap<TypeSpec, String>() {{
        put(TypeSpec.BOOLEAN, "boolean");
        put(TypeSpec.DATE, "\\Illuminate\\Support\\Carbon");
        put(TypeSpec.INTEGER, "int");
        put(TypeSpec.FILE, "\\Illuminate\\Http\\UploadedFile");
        put(TypeSpec.FLOAT, "float");
        put(TypeSpec.STRING, "string");
    }};

    private HashMap<TypeSpec, String> typeMap = new HashMap<TypeSpec, String>() {{
        put(TypeSpec.BOOLEAN, "Constants::BOOLEAN");
        put(TypeSpec.DATE, "Constants::DATE");
        put(TypeSpec.INTEGER, "Constants::INTEGER");
        put(TypeSpec.FILE, "Constants::FILE");
        put(TypeSpec.FLOAT, "Constants::FLOAT");
        put(TypeSpec.STRING, "Constants::STRING");
        put(TypeSpec.MODEL, "Constants::MODEL");
        put(TypeSpec.ENUM, "Constants::ENUM");
    }};

    @Override
    String getName() {
        return "laravel";
    }

    @Override
    void postConstruct() {
        boFolder = env.getProperty("generator.writers.laravel.bo-folder", "BOs");
        serviceFolder = env.getProperty("generator.writers.laravel.service-folder", "Services");
        serviceSuffix = env.getProperty("generator.writers.laravel.service-suffix", "Service");

        valueHelperNamespace = env.getProperty(
            "generator.writers.laravel.value-helper-namespace",
            "Kamicloud\\StubApi\\Concerns\\ValueHelper"
        );

        baseDTONamespace = env.getProperty(
            "generator.writers.laravel.base-dto-namespace",
            "Kamicloud\\StubApi\\DTOs\\DTO"
        );

        baseMessageNamespace = env.getProperty(
            "generator.writers.laravel.base-message-namespace",
            "Kamicloud\\StubApi\\Http\\Messages\\Message"
        );

        baseEnumNamespace = env.getProperty(
            "generator.writers.laravel.base-enum-namespace",
            "Kamicloud\\StubApi\\BOs\\Enum"
        );

        baseExceptionNamespace = env.getProperty(
            "generator.writers.laravel.base-exception-namespace",
            "Kamicloud\\StubApi\\Exceptions\\BaseException"
        );

        String laravelPath = Objects.requireNonNull(env.getProperty("generator.writers.laravel.path"));
        outputDir = new File(laravelPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        generatedDir = new File(outputDir.getAbsolutePath() + "/app/Generated");
        routePath = new File(outputDir.getAbsolutePath() + "/routes/generated_routes.php");
        FileUtil.deleteAllFilesOfDir(generatedDir);
    }

    @Override
    public void update(OutputStub output) {
        output.getTemplates().forEach((version, templateStub) -> {
            try {
                ClassCombiner.setNamespacePathTransformer(this);
                writeHttp(version, templateStub);
                writeModels(version, templateStub);
                writeEnums(version, templateStub.getEnums());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        try {

            FileCombiner.build(
                outputDir.getAbsolutePath() + "/routes/generated_resource_routes.php",
                stringUtil.renderTemplate("laravel/restful/routes", output)
            );
            writeErrors(output);
            writeRoute(output);

            writeEnums(boFolder, output.getCurrentTemplate().getEnums().stream().filter(enumStub -> {
                return enumStub.hasAnnotation(AsBO.class);
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeModels(String version, TemplateStub templateStub) {
        templateStub.getModels().forEach((modelStub) -> {
            String modelName = modelStub.getName();
            if (
                modelStub.hasAnnotation(AsBO.class) &&
                    templateStub.isCurrent()
            ) {
                writeModel(boFolder, modelStub);
            }
            writeModel(version, modelStub);
        });
    }

    private void writeModel(String version, ModelStub modelStub) {
        try {
            String modelName = modelStub.getName();
            ClassCombiner modelClassCombiner = new ClassCombiner(
                "App\\Generated\\" + version + "\\" + modelStub.getDtoFolder() + "\\" + modelName + modelStub.getDtoSuffix(),
                baseDTONamespace
            );

            modelClassCombiner.addTrait(valueHelperNamespace);

            LinkedList<ParameterStub> parameters = modelStub.getParameters();

            writeParameterAttributes(parameters, modelClassCombiner);
            writeParameterGetters(parameters, modelClassCombiner);
            writeParameterSetters(parameters, modelClassCombiner);
            writeGetAttributeMapMethod(version, "getAttributeMap", parameters, modelClassCombiner);

            modelClassCombiner.toFile();


            if (modelStub.isResource()) {
                writeRESTFul(version, modelStub);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeRESTFul(String version, ModelStub modelStub) {
        String modelName = modelStub.getName();

        try {
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/" + version + "/Messages/RESTFul/" + modelName + "/IndexMessage.php",
                stringUtil.renderTemplate("laravel/restful/index", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/" + version + "/Messages/RESTFul/" + modelName + "/UpdateMessage.php",
                stringUtil.renderTemplate("laravel/restful/update", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/" + version + "/Messages/RESTFul/" + modelName + "/ShowMessage.php",
                stringUtil.renderTemplate("laravel/restful/show", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/" + version + "/Messages/RESTFul/" + modelName + "/StoreMessage.php",
                stringUtil.renderTemplate("laravel/restful/store", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/" + version + "/Messages/RESTFul/" + modelName + "/DestroyMessage.php",
                stringUtil.renderTemplate("laravel/restful/destroy", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Generated/Controllers/" + version + "/RESTFul/" + modelName + "Controller.php",
                stringUtil.renderTemplate("laravel/restful/controller", modelStub),
                true
            );
            FileCombiner.build(
                outputDir.getAbsolutePath() + "/app/Http/Services/" + version + "/RESTFul/" + modelName + "Service.php",
                stringUtil.renderTemplate("laravel/restful/service", modelStub)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * HTTP层模板生成
     * <p>
     * 1、不使用
     *
     * @param o 模板解析文件
     */
    private void writeHttp(String version, TemplateStub o) {
        o.getControllers().forEach(controllerStub -> {
            try {
                String serviceClassName = "App\\Http\\" + serviceFolder + "\\" + version + "\\" + controllerStub.getName() + serviceSuffix;
                ClassCombiner controllerClassCombiner = new ClassCombiner(
                    "App\\Generated\\Controllers\\" + version + "\\" + controllerStub.getName() + "Controller",
                    "App\\Http\\Controllers\\Controller"
                );

                new ClassAttributeCombiner(controllerClassCombiner, "handler", "public");

                ClassMethodCombiner constructor = ClassMethodCombiner.build(
                    controllerClassCombiner,
                    "__construct",
                    "public"
                ).setBody(
                    "$this->handler = $handler;"
                );

                new ClassMethodParameterCombiner(constructor, "handler", serviceClassName);

                controllerStub.getActions().forEach((action) -> {
                    String actionName = action.getName();

                    try {
                        String messageClassName = "App\\Generated\\" + version + "\\Messages\\" + controllerStub.getName() + "\\" + actionName + "Message";

                        ClassCombiner messageClassCombiner = new ClassCombiner(
                            messageClassName,
                            baseMessageNamespace
                        );

                        String lowerCamelActionName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, action.getName());

                        controllerClassCombiner.addUse(serviceClassName);
                        controllerClassCombiner.addUse("DB");

                        ClassMethodCombiner actionClassMethodCombiner = new ClassMethodCombiner(controllerClassCombiner, lowerCamelActionName);
                        new ClassMethodParameterCombiner(actionClassMethodCombiner, "message", messageClassName);

                        String getResponseMethod = "getResponse";
                        if (action.hasAnnotation(FileResponse.class)) {
                            getResponseMethod = "getFileResponse";
                        }

                        actionClassMethodCombiner.setBody(
                            "$message->validateInput();",
                            "$this->handler->" + lowerCamelActionName + "($message);",
                            "$message->validateOutput();",
                            "return $message->" + getResponseMethod + "();"
                        );
                        if (action.hasAnnotation(Transactional.class)) {
                            actionClassMethodCombiner.wrapBody(
                                "return DB::transaction(function () use ($message) {",
                                "});"
                            );
                        }

                        messageClassCombiner.addTrait(valueHelperNamespace);
                        // message
                        writeParameterGetters(action.getRequests(), messageClassCombiner);
                        writeParameterAttributes(action.getRequests(), messageClassCombiner);
                        writeParameterAttributes(action.getResponses(), messageClassCombiner);
                        writeGetAttributeMapMethod(version, "requestRules", action.getRequests(), messageClassCombiner);
                        writeGetAttributeMapMethod(version, "responseRules", action.getResponses(), messageClassCombiner);
                        if (action.hasAnnotation(FileResponse.class)) {
                            ClassMethodCombiner setResponseMethod = new ClassMethodCombiner(messageClassCombiner, "setFileResponse");
                            new ClassMethodParameterCombiner(setResponseMethod, "fileResponse");
                            setResponseMethod.addBody("$this->fileResponse = $fileResponse;");
                        } else {
                            ClassMethodCombiner setResponseMethod = new ClassMethodCombiner(messageClassCombiner, "setResponse");
                            writeMethodParameters(action.getResponses(), setResponseMethod);
                        }

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

    private void writeEnums(String version, List<EnumStub> enumStubs) {
        enumStubs.forEach(enumStub -> {
            try {
                ClassCombiner enumClassCombiner = new ClassCombiner(
                    "App\\Generated\\" + version + "\\Enums\\" + enumStub.getName(),
                    baseEnumNamespace
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

                    if (enumStub.hasAnnotation(StringEnum.class)) {
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
                ClassConstantCombiner constant = new ClassConstantCombiner(
                    CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, error.getName()),
                    null
                );
                constant.addLine(error.getCode());
                errorCodeClassCombiner.addConstant(constant);
                String exceptionName = error.getName();
                ClassCombiner exceptionClassCombiner = new ClassCombiner(
                    "App\\Generated\\Exceptions\\" + exceptionName + "Exception",
                    baseExceptionNamespace
                );

                ClassMethodCombiner constructMethodCombiner = new ClassMethodCombiner(exceptionClassCombiner, "__construct");
                new ClassMethodParameterCombiner(constructMethodCombiner, "message", null, "null");
                constructMethodCombiner.addBody(
                    "parent::__construct($message, ErrorCode::" +
                    CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, error.getName()) +
                    ");"
                );

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
            templateStub.getControllers().forEach(controller -> controller.getActions().forEach((action) -> {
                String actionName = action.getName();

                AnnotationStub methodsAnnotation = action.getAnnotation(Methods.class);
                ArrayList<String> allowMethods;
                String method;
                if (methodsAnnotation != null) {
                    allowMethods = methodsAnnotation.getValues();
                    method = "['" + String.join("', '", allowMethods) + "', 'POST']";
                } else {
                    method = "['POST']";
                }
                String middlewarePart = "";
                if (action.hasAnnotation(Middleware.class)) {
                    AnnotationStub x = action.getAnnotation(Middleware.class);
                    middlewarePart = "->middleware(['" + String.join("', '", x.getValues()) + "'])";
                }
                fileCombiner.addLine(
                    "Route::match(" + method + ", '" + action.getUri() + "', '" + version + "\\" + controller.getName() + "Controller@" + actionName + "')" + middlewarePart + ";"
                );
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

    private void writeParameterAttributes(LinkedList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterStub) -> new ClassAttributeCombiner(classCombiner, parameterStub.getName(), "protected"));
    }

    private void writeParameterGetters(LinkedList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterStub) -> {
            writeParameterGetter(
                parameterStub,
                classCombiner,
                "get"
            );

            if (parameterStub.isBoolean()) {
                writeParameterGetter(
                    parameterStub,
                    classCombiner,
                    "is"
                );
            }
        });
    }

    private void writeParameterGetter(ParameterStub parameterStub, ClassCombiner classCombiner, String prefix) {
        Type type = parameterStub.getType();

        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());

        parameterName = prefix + parameterName;

        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, parameterName);
        classMethodCombiner.setBody("return $this->" + parameterStub.getName() + ";");

        classMethodCombiner.addComment(parameterStub.getComment());

        String returnType;

        if (type.getSpec() == TypeSpec.MODEL) {
            returnType = parameterStub.getTypeSimpleName() + parameterStub.getDtoSuffix();
        } else if (type.getSpec() == TypeSpec.ENUM || type.getSpec() == TypeSpec.DATE) {
            returnType = "mixed";
        } else {
            returnType = returnTypeMap.get(type.getSpec());
        }

        if (parameterStub.isArray()) {
            returnType += "[]";
        }

        if (parameterStub.hasAnnotation(Optional.class)) {
            returnType += "|null";
        }

        classMethodCombiner.addComment("@return " + returnType);
    }

    private void writeParameterSetters(LinkedList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterStub) -> writeParameterSetter(parameterStub, classCombiner));
    }

    private void writeParameterSetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, "set" + parameterName);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
        new ClassMethodParameterCombiner(classMethodCombiner, parameterStub.getName());
    }

    private void writeMethodParameters(LinkedList<ParameterStub> parameters, ClassMethodCombiner classMethodCombiner) {
        parameters.forEach((parameterStub) -> writeMethodParameter(parameterStub, classMethodCombiner));
    }

    private void writeMethodParameter(ParameterStub parameterStub, ClassMethodCombiner classMethodCombiner) {
        new ClassMethodParameterCombiner(classMethodCombiner, parameterStub.getName());

        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
    }

    /**
     * @param parameters    参数
     * @param classCombiner 目标类
     */
    private void writeGetAttributeMapMethod(String version, String methodName, LinkedList<ParameterStub> parameters, ClassCombiner classCombiner) {
        classCombiner.addUse("Kamicloud\\StubApi\\Utils\\Constants");
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, methodName);

        parameters.forEach((parameterStub) -> {
            String parameterName = parameterStub.getName();
            String typeName = parameterStub.getTypeSimpleName();
            String rule;
            ArrayList<String> ruleList = new ArrayList<String>() {{
                add("bail");
            }};
            ArrayList<String> types = new ArrayList<String>(){{
                add(typeMap.get(parameterStub.getTypeSpec()));
            }};
            boolean isArray = parameterStub.isArray();
            boolean isModel = parameterStub.isModel();
            boolean isEnum = parameterStub.isEnum();
            if (parameterStub.hasAnnotation(Optional.class)) {
                types.add("Constants::OPTIONAL");
                ruleList.add("nullable");
            }
            if (parameterStub.hasAnnotation(Mutable.class)) {
                types.add("Constants::MUTABLE");
            }
            if (isArray) {
                types.add("Constants::ARRAY");
            }
            if (isModel) {
                classCombiner.addUse("App\\Generated\\" + version + "\\" + parameterStub.getDtoFolder() + "\\" + typeName + parameterStub.getDtoSuffix());
                rule = typeName + parameterStub.getDtoSuffix() + "::class";
            } else if (isEnum) {
                classCombiner.addUse("App\\Generated\\" + version + "\\Enums\\" + typeName);
                rule = typeName + "::class";
            } else {
                Type typeInstance = parameterStub.getType();

                ruleList.add(typeInstance.getLaravelRule());
                rule = "'" + String.join("|", ruleList) + "'";
            }

            String dbField = isModel ? parameterName :
                stringUtil.lowerCamelToLowerUnderscore(parameterName);

            AnnotationStub annotationStub = parameterStub.getAnnotation(DBField.class);

            /* 如果参数有指定的映射关系，使用指定的映射 */
            if (annotationStub != null) {
                String fieldValue = annotationStub.getValue();
                if (!fieldValue.equals("")) {
                    dbField = fieldValue;
                }
            }

            String laravelParam = parameterStub.getType().getLaravelParam();

            ArrayList<String> params = new ArrayList<>(Arrays.asList(
                "'" + parameterName + "'",
                "'" + dbField + "'",
                rule,
                types.isEmpty() ? "null" : String.join(" | ", types),
                laravelParam == null ? "null" : ("'" + laravelParam + "'")
            ));
            classMethodCombiner.addBody("[" + String.join(", ", params) + "],");
        });
        classMethodCombiner.wrapBody("return [", "];");
    }
}
