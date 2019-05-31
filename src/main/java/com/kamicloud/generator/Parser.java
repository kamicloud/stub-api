package com.kamicloud.generator;

import com.kamicloud.generator.stubs.*;
import definitions.annotations.ErrorInterface;
import definitions.annotations.FixedEnumValueInterface;
import definitions.annotations.Request;
import definitions.types.CustomizeInterface;
import templates.TemplateList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Parser {
    public OutputStub parse() {
        OutputStub output = new OutputStub();

        Arrays.asList(TemplateList.templates).forEach(template -> parseTemplate(template, output));
        parseErrors(TemplateList.errorsTemplate, output);

        return output;
    }

    private void parseTemplate(Class<?> template, OutputStub outputStub) {
        String version = template.getSimpleName();
        version = version.replace("Template", "");
        TemplateStub templateStub = new TemplateStub(version);

        Arrays.asList(template.getDeclaredClasses()).forEach(part -> {
            if (part.getSimpleName().equals("Enums")) {
                parseEnums(part.getDeclaredClasses(), templateStub);
            } else if (part.getSimpleName().equals("Controllers")) {
                parseControllers(part.getDeclaredClasses(), templateStub);
            } else if (part.getSimpleName().equals("Models")) {
                parseModels(part.getDeclaredClasses(), templateStub);
            }
        });

        parseComment(template.getCanonicalName(), templateStub);

        outputStub.addTemplate(templateStub);

        if (template == TemplateList.currentTemplate) {
            outputStub.setCurrentTemplate(templateStub);
        }
    }

    private void parseErrors(Class<? extends Enum> errorsTemplate, OutputStub templateStub) {
        Arrays.asList(errorsTemplate.getFields()).forEach(error -> {
            try {
                Enum<?> value = Enum.valueOf(errorsTemplate, error.getName());
                if (ErrorInterface.class.isAssignableFrom(errorsTemplate)) {
                    Method getValue = errorsTemplate.getMethod("getValue");
                    String fillValue = getValue.invoke(value).toString();


                    ErrorStub errorStub = new ErrorStub(
                        error.getName(),
                        fillValue,
                        ""
                    );
                    parseAnnotations(error.getAnnotations(), errorStub);
                    parseComment(fieldBuilder(error), errorStub);
                    templateStub.addError(errorStub);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void parseControllers(Class<?>[] controllers, TemplateStub templateStub) {
        Arrays.asList(controllers).forEach(controller -> {
            ControllerStub controllerStub = new ControllerStub(controller.getSimpleName());
            templateStub.addController(controllerStub);

            parseAnnotations(controller.getAnnotations(), controllerStub);
            parseComment(controller.getCanonicalName(), controllerStub);

            Arrays.asList(controller.getDeclaredClasses()).forEach(action -> {
                ActionStub actionStub = new ActionStub(action.getSimpleName());
                controllerStub.addAction(actionStub);
                // 注解
                parseAnnotations(action.getAnnotations(), actionStub);

                parseComment(action.getCanonicalName(), actionStub);

                // 遍历每一个参数，注解+类型+变量
                Arrays.asList(action.getDeclaredFields()).forEach(parameter -> {
                    ParameterStub parameterStub = parseParameter(parameter);
                    if (parameterStub != null) {
                        if (parameterStub.hasAnnotation(Request.class)) {
                            actionStub.addRequest(parameterStub);
                        } else {
                            actionStub.addResponse(parameterStub);
                        }
                    }
                });
            });
        });
    }

    private void parseEnums(Class<?>[] enumsTemplate, TemplateStub templateStub) {
        Arrays.asList(enumsTemplate).forEach(enumTemplate -> {
            EnumStub enumStub = new EnumStub(enumTemplate.getSimpleName());
            templateStub.addEnum(enumStub);

            parseComment(enumTemplate.getCanonicalName(), enumStub);
            parseAnnotations(enumTemplate.getAnnotations(), enumStub);
            try {
                Class clazz = Class.forName(enumTemplate.getName());

                Arrays.asList(enumTemplate.getFields()).forEach(entryTemplate -> {
                    try {
                        String key = entryTemplate.getName();
                        Enum<?> value = Enum.valueOf(clazz, entryTemplate.getName());
                        Integer ordinal = value.ordinal();
                        EnumStub.EnumStubItemType type = EnumStub.EnumStubItemType.INTEGER;
                        String fillValue = ordinal.toString();
                        if (FixedEnumValueInterface.class.isAssignableFrom(enumTemplate)) {
                            Method getValue = enumTemplate.getMethod("getValue");
                            getValue.setAccessible(true);
                            fillValue = getValue.invoke(value).toString();
                        }
                        EnumStub.EnumStubItem item = new EnumStub.EnumStubItem(fillValue, type);
                        enumStub.addItem(key, item);

                        parseComment(fieldBuilder(entryTemplate), item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void parseAnnotations(Annotation[] annotations, AnnotationsInterface baseStub) {
        Arrays.asList(annotations).forEach(annotation -> {
            Class<?> annotationClass = annotation.annotationType();
            String annotationName = annotationClass.getSimpleName();

            AnnotationStub annotationStub = new AnnotationStub(annotationName);

            baseStub.addAnnotation(annotation, annotationStub);

            List<Method> annotationMethods = Arrays.asList(annotationClass.getDeclaredMethods());

            annotationMethods.forEach(method -> {
                try {
                    Object value = method.invoke(annotation);
                    Object[] values;
                    Class<?> valueClass = value.getClass();

                    if (valueClass.isArray()) {
                        values = (Object[]) value;
                        Arrays.asList(values).forEach(subValue -> {
                            annotationStub.addValue(subValue.toString());
                        });
                    } else {
                        annotationStub.setValue(value.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void parseModels(Class<?>[] models, TemplateStub templateStub) {
        Arrays.asList(models).forEach(model -> {
            ModelStub modelStub = new ModelStub(model.getSimpleName());
//            // 继承关系
//            NodeList extendedTypeTemplates = ((ClassOrInterfaceDeclaration) modelTemplate).getExtendedTypes();
//            if (!extendedTypeTemplates.isEmpty()) {
//                modelStub.setExtendsFrom(extendedTypeTemplates.get(0).toString());
//            }

            templateStub.addModel(modelStub);
            Generator.modelHashMap.put(model.getCanonicalName(), modelStub);
            // 注解
            parseAnnotations(model.getAnnotations(), modelStub);
            parseComment(model.getCanonicalName(), modelStub);

            parseComment(model.getCanonicalName(), modelStub);
            modelStub.setParentKey(model.getSuperclass().getCanonicalName());
            // 遍历每一个参数，注解+类型+变量
            Arrays.asList(model.getDeclaredFields()).forEach(parameter -> {
                ParameterStub parameterStub = parseParameter(parameter);
                if (parameterStub != null) {
                    modelStub.addParameter(parameterStub);
                }

            });
        });
    }

    private ParameterStub parseParameter(Field parameter) {
        if (parameter.getName().startsWith("this")) {
            return null;
        }

        // 类型+变量
        Class<?> parameterType = parameter.getType();
        String typeName = parameterType.getName();
        String typeSimpleName = parameterType.getSimpleName();

        if (Arrays.asList(parameterType.getInterfaces()).contains(CustomizeInterface.class)) {
            try {
                CustomizeInterface x = (CustomizeInterface) parameterType.newInstance();
                typeSimpleName = x.getType();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        ParameterStub parameterStub = new ParameterStub(parameter.getName(), typeSimpleName);
        if (typeName.contains("$Models$")) {
            parameterStub.setModel(true);
        } else if (typeName.contains("$Enums$")) {
            parameterStub.setEnum(true);
        }
        parameterStub.setArray(parameterType.isArray());

        // 注解
        parseAnnotations(parameter.getAnnotations(), parameterStub);
        parseComment(fieldBuilder(parameter), parameterStub);

        return parameterStub;
    }

    private void parseComment(String name, BaseWithAnnotationStub commentStub) {
        commentStub.setClasspath(name);
        Generator.classHashMap.add(commentStub);
    }

    private String fieldBuilder(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "." + field.getName();
    }
}
