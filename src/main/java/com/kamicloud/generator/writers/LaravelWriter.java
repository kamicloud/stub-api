package com.kamicloud.generator.writers;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.annotations.Optional;
import com.kamicloud.generator.annotations.Transactional;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.AnnotationStub;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.TemplateStub;
import com.kamicloud.generator.stubs.ParameterStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.php.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
                    "App\\Abstracts\\BaseModel"
                );

                modelClassCombiner.addTrait("App\\Traits\\QueryData");
                modelClassCombiner.addImplement("JsonSerializable");
                writeParameterAttributes(modelStub.getParameters(), modelClassCombiner);
                writeParameterGetters(modelStub.getParameters(), modelClassCombiner);
                writeParameterSetters(modelStub.getParameters(), modelClassCombiner);
                writeParameterRules("rules", modelStub.getParameters(), modelClassCombiner);
                writeGetAttributeMapMethod(modelStub.getParameters(), modelClassCombiner);
//                writeInitFromEloquentMethod(modelStub.getParameters(), modelClassCombiner);

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
                        String requestClassName = "Request";
                        String messageClassName = actionName + "Message";

                        ClassCombiner messageClassCombiner = new ClassCombiner(
                                "App\\Generated\\" + version + "\\Messages\\" + controllerStub.getName() + "\\" + messageClassName,
                                "App\\Abstracts\\BaseMessage"
                        );

                        String lowerCamelActionName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, action.getName());

                        controllerClassCombiner.addUse("Illuminate\\Http\\Request");
                        controllerClassCombiner.addUse("App\\Generated\\" + version + "\\Messages\\" + controllerStub.getName() + "\\" + messageClassName);
                        controllerClassCombiner.addUse("App\\Http\\Services\\" + version + "\\" + controllerStub.getName() + "Service");
                        controllerClassCombiner.addUse("DB");


                        ClassMethodCombiner actionClassMethodCombiner = new ClassMethodCombiner(lowerCamelActionName);
                        actionClassMethodCombiner.addParameter(new ClassMethodParameterCombiner("request", requestClassName));

                        controllerClassCombiner.addMethod(actionClassMethodCombiner);

                        actionClassMethodCombiner.setBody(
                                "$message = new " + action.getName() + "Message($request);",
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

                        ClassMethodCombiner constructClassMethodCombiner = new ClassMethodCombiner("__construct");
                        constructClassMethodCombiner.addParameter(new ClassMethodParameterCombiner("request", requestClassName));
                        constructClassMethodCombiner.setBody(
                            "$this->request = $request;\n",
                            "$data = $request->all();\n"
                        );

                        action.getRequests().forEach((parameterName, parameterStub) -> {
                            String dataInArray = "$data['" + parameterStub.getName() + "']";
                            constructClassMethodCombiner.addBody(
                                "$this->" + parameterStub.getName() + " = " + dataInArray + " ?? null;"
                            );

                            writeModelSerialize(parameterStub, constructClassMethodCombiner, messageClassCombiner);
                        });

                        messageClassCombiner.addMethod(constructClassMethodCombiner);
                        messageClassCombiner.addMethod(new ClassMethodCombiner("validateInput"));
                        messageClassCombiner.addMethod(new ClassMethodCombiner("validateOutput"));

                        ClassMethodCombiner setResponseMethod = new ClassMethodCombiner("setResponse");
                        ClassMethodCombiner getResponseClassMethodCombiner = new ClassMethodCombiner("getResponse");

                        // message
                        messageClassCombiner.addUse("Illuminate\\Http\\Request");
                        messageClassCombiner.addAttribute(new ClassAttributeCombiner("request", "private"));
                        writeParameterGetters(action.getRequests(), messageClassCombiner);
                        writeParameterAttributes(action.getRequests(), messageClassCombiner);

                        writeParameterAttributes(action.getResponses(), messageClassCombiner);
                        action.getResponses().forEach((parameterName, parameterStub) -> getResponseClassMethodCombiner.addBody("'"  + parameterStub.getName() + "' => $this->" + parameterStub.getName() + ","));
                        getResponseClassMethodCombiner.wrapBody(new ArrayList<>(Arrays.asList(
                            "'status' => 0,",
                            "'message' => 'success',",
                            "'data' => ["
                        )), "],");
                        getResponseClassMethodCombiner.wrapBody(
                                "return [",
                                "];"
                        );

                        writeMethodParameters(action.getResponses(), setResponseMethod);
                        writeParameterRules("requestRules", action.getRequests(), messageClassCombiner);
                        writeParameterRules("responseRules", action.getResponses(), messageClassCombiner);
                        messageClassCombiner.addMethod(setResponseMethod).addMethod(getResponseClassMethodCombiner);

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
                        "App\\Generated\\" + version + "\\Enums\\" + enumStub.getName() + "Enum"
                );
                enumStub.getItems().forEach((key, value) -> {
                    ClassConstantCombiner enumClassConstantCombiner = new ClassConstantCombiner(key, value.getName(), value.getType());
                    enumClassCombiner.addConstant(enumClassConstantCombiner);
                });


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
                errorCodeClassCombiner.addConstant(new ClassConstantCombiner(error.getName(), error.getCode(), null));
                String exceptionName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, error.getName());
                ClassCombiner exceptionClassCombiner = new ClassCombiner(
                        "App\\Generated\\Exceptions\\" + exceptionName + "Exception",
                        "App\\Exceptions\\BaseException"
                );

                ClassMethodCombiner constructMethodCombiner = new ClassMethodCombiner("__construct");
                constructMethodCombiner.addParameter(new ClassMethodParameterCombiner("message", null, "null"));
                constructMethodCombiner.addBody("parent::__construct($message, ErrorCode::" + error.getName() + ");");

                exceptionClassCombiner.addMethod(constructMethodCombiner);

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
            try {
                outputStreamWriter.write("Route::post('/" + version + "/" + controller.getName() + "/" + actionName + "', '" + version + "\\" + controller.getName() + "Controller@" + actionName + "');\n");
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
        parameters.forEach((parameterName, parameterStub) -> writeParameterAttribute(parameterStub, classCombiner));
    }

    private void writeParameterAttribute(ParameterStub parameterStub, ClassCombiner classCombiner) {
        classCombiner.addAttribute(new ClassAttributeCombiner(parameterStub.getName(), "private"));
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
        ClassMethodCombiner cn = new ClassMethodCombiner(parameterName);
        cn.setBody("return $this->" + parameterStub.getName() + ";");
        classCombiner.addMethod(cn);
    }

    private void writeParameterSetters(HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach((parameterName, parameterStub) -> writeParameterSetter(parameterStub, classCombiner));
    }

    private void writeParameterSetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner("set" + parameterName);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
        classMethodCombiner.addParameter(new ClassMethodParameterCombiner(parameterStub.getName()));
        classCombiner.addMethod(classMethodCombiner);
    }

    private void writeParameterRules(String methodName, HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(methodName);
        parameters.forEach((parameterName, parameterStub) -> {
            ArrayList<String> ruleList = new ArrayList<>();
            String typeName = parameterStub.getType();
            // 参数校验
            ruleList.add("bail");
            if (!parameterStub.hasAnnotation(Optional.name)) {
                ruleList.add("required");
            }
            if (typeName.endsWith("[]")) {
                typeName = typeName.replace("[]", "");
                typeName = typeName.replace(".", "[].");
            }
            if (typeName.startsWith("Models.")) {
                typeName = typeName.replace('.', ':');
            }

            ruleList.add(typeName);
            classMethodCombiner.addBody("'" + parameterName + "' => '" + String.join("|", ruleList) + "',");
        });

        classMethodCombiner.wrapBody("return [", "];");
        classCombiner.addMethod(classMethodCombiner);
    }

    private void writeMethodParameters(HashMap<String, ParameterStub> parameters, ClassMethodCombiner classMethodCombiner) {
        parameters.forEach((parameterName, parameterStub) -> writeMethodParameter(parameterStub, classMethodCombiner));
    }

    private void writeMethodParameter(ParameterStub parameterStub, ClassMethodCombiner classMethodCombiner) {
        ClassMethodParameterCombiner classMethodParameterCombiner = new ClassMethodParameterCombiner(parameterStub.getName());

        classMethodCombiner.addParameter(classMethodParameterCombiner);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
    }

    /**
     *
     * @param parameters 参数
     * @param classCombiner 目标类
     */
    private void writeGetAttributeMapMethod(HashMap<String, ParameterStub> parameters, ClassCombiner classCombiner) {
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner("getAttributeMap");


        parameters.forEach((parameterName, parameterStub) -> {
            String typeName = parameterStub.getType();
            boolean isArray = false;
            boolean isModel = false;
            if (parameterStub.getType().endsWith("[]")) {
                isArray = true;
            }
            if (parameterStub.getType().startsWith("Models")) {
                isModel = true;
            }
            ArrayList<String> params = new ArrayList<>(Arrays.asList(
                "'" + parameterName + "'",
                "'" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parameterName) + "'", // DBField
                Boolean.toString(isModel),
                Boolean.toString(isArray),
                "'" + typeName + "'"
            ));
            classMethodCombiner.addBody("[" + String.join(", ", params) + "],");
        });
        classMethodCombiner.wrapBody("return [", "];");
        classCombiner.addMethod(classMethodCombiner);
    }

    private String getModelNameFromType(String type) {
        return type.replace("[]", "").replace("Models.", "");
    }

    private void writeModelSerialize(
        ParameterStub parameterStub,
        ClassMethodCombiner classMethodCombiner,
        ClassCombiner classCombiner
    ) {
        String parameterName = parameterStub.getName();
        String parameterType = parameterStub.getType();
        String parameterInModel = "$this->" + parameterName;
        boolean isArray = parameterType.endsWith("[]");
        boolean isScalar = !parameterType.startsWith("Models.[]");
        if (parameterType.startsWith("Models.")) {
            parameterType = getModelNameFromType(parameterType);
            classCombiner.addUse("App\\Generated\\" + version + "\\Models\\" + parameterType + "Model");

            classMethodCombiner.addBody(
                parameterInModel + " = " +
                parameterType + "Model::fromJsonModel" + (isArray ? "s" : "") +
                "(" + parameterInModel +
                (isScalar ? ", " + parameterType + "Model::class" : "") +
                ");"
            );
        }
    }
}
