package com.zhh.generator.writers;

import com.google.common.base.CaseFormat;
import com.zhh.generator.annotations.Optional;
import com.zhh.generator.annotations.Transactional;
import com.zhh.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.zhh.generator.stubs.AnnotationStub;
import com.zhh.generator.stubs.OutputStub;
import com.zhh.generator.stubs.TemplateStub;
import com.zhh.generator.stubs.ParameterStub;
import com.zhh.generator.utils.FileUtil;
import com.zhh.generator.writers.components.php.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;

public class LaravelWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private String version;

    private File outputDir = new File(dir.getAbsolutePath() + "/src/main/php/laravel");
    private File appDir = new File(outputDir.getAbsolutePath() + "/app");
    private File servicesDir = new File(appDir.getAbsolutePath() + "/Http/Services");
    private File serviceVersionDir;
    private File generatedDir = new File(appDir.getAbsolutePath() + "/Generated");
    private File generatedVersionDir;

    private File routePath = new File(outputDir.getAbsolutePath() + "/routes/generated_routes.php");
    private File exceptionsDir = new File(generatedDir.getAbsolutePath() + "/Exceptions");
    private File enumsDir;
    private File modelsDir;
    private File businessObjectsDir;
    private File httpDir;
    private File controllersDir;
    private File messagesDir;

    @Override
    public void update(Observable o, Object arg) {
        OutputStub output = (OutputStub) o;
        FileUtil.deleteAllFilesOfDir(generatedDir);
        ((OutputStub) o).getTemplates().forEach((version, templateStub) -> {
            boolean res;
            this.version = version;
            serviceVersionDir = new File(servicesDir.getAbsolutePath() + "/" + version);
            generatedVersionDir = new File(generatedDir.getAbsolutePath() + "/" + version);

            enumsDir = new File(generatedVersionDir.getAbsolutePath() + "/Enums");
            modelsDir = new File(generatedVersionDir.getAbsolutePath() + "/Models");
            businessObjectsDir = new File(generatedDir.getAbsolutePath() + "/BusinessObjects");
            httpDir = new File(generatedDir.getAbsolutePath() + "/Controllers");
            controllersDir = new File(httpDir.getAbsolutePath() + "/" + version);
            messagesDir = new File(generatedVersionDir.getAbsolutePath() + "/Messages");

            res = servicesDir.mkdir();
            res = generatedDir.mkdir();
            res = serviceVersionDir.mkdir();
            res = generatedVersionDir.mkdir();
            res = routePath.delete();
            res = exceptionsDir.mkdir();
            res = enumsDir.mkdir();
            res = modelsDir.mkdir();
            res = businessObjectsDir.mkdir();
            res = httpDir.mkdir();
            res = messagesDir.mkdir();
            res = controllersDir.mkdir();

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

    private void writeModels(TemplateStub o) throws IOException {
        o.getModels().forEach(modelStub -> {
            try {
                ClassCombiner modelClassCombiner = new ClassCombiner(
                    "App\\Generated\\" + version + "\\Models\\" + modelStub.getName() + "Model",
                    "App\\Abstracts\\BaseModel"
                );

                modelClassCombiner.addUse("App\\Traits\\QueryData");
                modelClassCombiner.addTrait("QueryData");
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
     * @throws IOException 异常
     */
    private void writeHttp(TemplateStub o) throws IOException {
        o.getControllers().forEach(controllerStub -> {
            try {
                (new File(messagesDir + "/" + controllerStub.getName())).mkdir();
//                (new File(requestsDir + "/" + controllerStub.getName())).mkdir();
                (new File(servicesDir + "/" + controllerStub.getName() + "Service.php")).createNewFile();
                ClassCombiner controllerClassCombiner = new ClassCombiner(
                    "App\\Generated\\Controllers\\" + version + "\\" + controllerStub.getName() + "Controller",
                    "App\\Http\\Controllers\\Controller"
                );


                controllerStub.getActions().forEach(action -> {
                    try {
                        String requestClassName = "Request";
                        String messageClassName = action.getName() + "Message";

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

                        actionClassMethodCombiner.setBody(new ArrayList<>(Arrays.asList(
                            "$message = new " + action.getName() + "Message($request);",
                            "$message->validateInput();",
                            controllerStub.getName() + "Service::" + lowerCamelActionName + "($message);",
                            "$message->validateOutput();",
                            "return $message->getResponse();"
                        )));
                        if (action.getAnnotations().contains(new AnnotationStub(Transactional.name))) {
                            actionClassMethodCombiner.wrapBody(new ArrayList<>(Collections.singletonList(
                                "return DB::transaction(function () use ($request) {"
                            )), new ArrayList<>(Collections.singletonList(
                                "});"
                            )));
                        }

                        ClassMethodCombiner constructClassMethodCombiner = new ClassMethodCombiner("__construct");
                        constructClassMethodCombiner.addParameter(new ClassMethodParameterCombiner("request", requestClassName));
                        constructClassMethodCombiner.setBody(new ArrayList<>(Arrays.asList(
                            "$this->request = $request;\n",
                            "$data = $request->all();\n"
                        )));

                        action.getRequests().forEach(parameterStub -> {
                            String dataInArray = "$data['" + parameterStub.getName() + "']";
                            constructClassMethodCombiner.addBody(
                                "$this->" + parameterStub.getName() + " = " + dataInArray + " ?? null;"
                            );

                            writeModelSerialize(parameterStub, constructClassMethodCombiner, messageClassCombiner);
//                            constructClassMethodCombiner.addBody("\n");
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
                        action.getResponses().forEach(parameterStub -> {
                            getResponseClassMethodCombiner.addBody("'"  + parameterStub.getName() + "' => $this->" + parameterStub.getName() + ",");
                        });
                        getResponseClassMethodCombiner.wrapBody(new ArrayList<>(Arrays.asList(
                            "'status' => 0,",
                            "'message' => 'success',",
                            "'data' => ["
                        )), new ArrayList<>(Collections.singletonList(
                            "],"
                        )));
                        getResponseClassMethodCombiner.wrapBody(new ArrayList<>(Collections.singletonList(
                            "return ["
                        )), new ArrayList<>(Collections.singletonList(
                            "];"
                        )));

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

    private void writeEnums(TemplateStub o) throws IOException {
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

        o.getControllers().forEach(controller -> {
            controller.getActions().forEach(action -> {
                try {
                    outputStreamWriter.write("Route::post('/" + version + "/" + controller.getName() + "/" + action.getName() + "', '" + version + "\\" + controller.getName() + "Controller@" + action.getName() + "');\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        outputStreamWriter.close();
        fileOutputStream.close();
    }

    private String getPathFromNamespace(String namespace, boolean isFile) {
        return outputDir.getAbsolutePath() + "/" + namespace.replace("App", "app").replace("\\", "/") + (isFile ? ".php" : "");
    }

    @Override
    public String namespaceToPath(String namespace) {
        return getPathFromNamespace(namespace, true);
    }

    @Override
    public String pathToNamespace(String path) {
        return null;
    }

    private void writeParameterAttributes(ArrayList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach(parameterStub -> writeParameterAttribute(parameterStub, classCombiner));
    }

    private void writeParameterAttribute(ParameterStub parameterStub, ClassCombiner classCombiner) {
        classCombiner.addAttribute(new ClassAttributeCombiner(parameterStub.getName(), "private"));
    }

    private void writeParameterGetters(ArrayList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach(parameterStub -> writeParameterGetter(parameterStub, classCombiner));
    }

    private void writeParameterGetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        if (parameterStub.getType().equals("boolean")) {
            parameterName = "is" + parameterName;
        } else {
            parameterName = "get" + parameterName;
        }
        ClassMethodCombiner cn = new ClassMethodCombiner(parameterName);
        cn.setBody(new ArrayList<>(Collections.singletonList(
            "return $this->" + parameterStub.getName() + ";"
        )));
        classCombiner.addMethod(cn);
    }

    private void writeParameterSetters(ArrayList<ParameterStub> parameters, ClassCombiner classCombiner) {
        parameters.forEach(parameterStub -> writeParameterSetter(parameterStub, classCombiner));
    }

    private void writeParameterSetter(ParameterStub parameterStub, ClassCombiner classCombiner) {
        String parameterName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterStub.getName());
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner("set" + parameterName);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
        classMethodCombiner.addParameter(new ClassMethodParameterCombiner(parameterStub.getName()));
        classCombiner.addMethod(classMethodCombiner);
    }

    private void writeParameterRules(String methodName, ArrayList<ParameterStub> parameters, ClassCombiner classCombiner) {
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(methodName);
        parameters.forEach(parameterStub -> {
            ArrayList<String> ruleList = new ArrayList<>();
            String parameterName = parameterStub.getName();
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

        classMethodCombiner.wrapBody(new ArrayList<>(Collections.singletonList("return [")), new ArrayList<>(Collections.singletonList("];")));
        classCombiner.addMethod(classMethodCombiner);
    }

    private void writeMethodParameters(ArrayList<ParameterStub> parameters, ClassMethodCombiner classMethodCombiner) {
        parameters.forEach(parameterStub -> writeMethodParameter(parameterStub, classMethodCombiner));
    }

    private void writeMethodParameter(ParameterStub parameterStub, ClassMethodCombiner classMethodCombiner) {
        String typeName = getModelNameFromType(parameterStub.getType());
        ClassMethodParameterCombiner classMethodParameterCombiner = new ClassMethodParameterCombiner(
            parameterStub.getName()
        );

        classMethodCombiner.addParameter(classMethodParameterCombiner);
        classMethodCombiner.addBody("$this->" + parameterStub.getName() + " = $" + parameterStub.getName() + ";");
    }

    /**
     *
     * @param parameters 参数
     * @param classCombiner 目标类
     */
    private void writeGetAttributeMapMethod(ArrayList<ParameterStub> parameters, ClassCombiner classCombiner) {
        ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner("getAttributeMap");


        parameters.forEach(parameterStub -> {
            String parameterName = parameterStub.getName();
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
        classMethodCombiner.wrapBody(new ArrayList<>(Collections.singletonList(
            "return ["
        )), new ArrayList<>(Collections.singletonList(
            "];"
        )));
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
        } else if (isArray) {

            return;
        }
    }
}
