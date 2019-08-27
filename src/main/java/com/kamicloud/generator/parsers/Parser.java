package com.kamicloud.generator.parsers;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.stubs.*;
import definitions.types.EnumType;
import definitions.types.ModelType;
import definitions.annotations.ErrorInterface;
import definitions.annotations.FixedEnumValueInterface;
import definitions.annotations.Request;
import definitions.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import templates.TemplateList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
    @Autowired
    OutputStub outputStub;
    /**
     * 标量数据对应的类型
     */
    private HashMap<String, Type> typeMap = new HashMap<String, Type>() {{
        put("int", new ScalarInteger());
        put("Integer", new ScalarInteger());
        put("long", new ScalarInteger());
        put("Long", new ScalarInteger());
        put("float", new ScalarFloat());
        put("Float", new ScalarFloat());
        put("double", new ScalarFloat());
        put("Double", new ScalarFloat());
        put("Number", new ScalarFloat());
        put("boolean", new ScalarBoolean());
        put("Boolean", new ScalarBoolean());
        put("Date", new ScalarDate());
        put("String", new ScalarString());
        put("File", new File());
    }};

    public void parse() {
        Arrays.asList(TemplateList.templates).forEach(template -> {
            parseTemplate(template);
        });
        parseErrors(TemplateList.errorsTemplate);
    }

    private void parseTemplate(Class<?> template) {
        String version = template.getSimpleName();
        version = version.replace("Template", "");
        TemplateStub templateStub = new TemplateStub(
            version,
            template.getCanonicalName()
        );

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

    private void parseErrors(Class<? extends Enum> errorsTemplate) {
        Arrays.asList(errorsTemplate.getFields()).forEach(error -> {
            try {
                Enum<?> value = Enum.valueOf(errorsTemplate, error.getName());
                if (ErrorInterface.class.isAssignableFrom(errorsTemplate)) {
                    Method getValue = errorsTemplate.getMethod("getValue");
                    String fillValue = getValue.invoke(value).toString();


                    ErrorStub errorStub = new ErrorStub(
                        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, error.getName()),
                        fieldBuilder(error),
                        fillValue,
                        ""
                    );
                    parseAnnotations(error.getAnnotations(), errorStub);
                    parseComment(fieldBuilder(error), errorStub);
                    outputStub.addError(errorStub);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void parseControllers(Class<?>[] controllers, TemplateStub templateStub) {
        Arrays.asList(controllers).forEach(controller -> {
            ControllerStub controllerStub = new ControllerStub(
                controller.getSimpleName(),
                controller.getCanonicalName()
            );
            templateStub.addController(controllerStub);

            parseAnnotations(controller.getAnnotations(), controllerStub);
            parseComment(controller.getCanonicalName(), controllerStub);

            Arrays.asList(controller.getDeclaredClasses()).forEach(action -> {
                ActionStub actionStub = new ActionStub(
                    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, action.getSimpleName()),
                    action.getCanonicalName()
                );
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
            EnumStub enumStub = new EnumStub(
                enumTemplate.getSimpleName(),
                enumTemplate.getCanonicalName()
            );
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
                        EnumStub.EnumStubItem item = new EnumStub.EnumStubItem(
                            fillValue,
                            fieldBuilder(entryTemplate),
                            type
                        );
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
            ModelStub modelStub = new ModelStub(
                model.getSimpleName(),
                model.getCanonicalName()
            );

            templateStub.addModel(modelStub);
            outputStub.modelHashMap.put(model.getCanonicalName(), modelStub);
            // 注解
            parseAnnotations(model.getAnnotations(), modelStub);
            parseComment(model.getCanonicalName(), modelStub);

            parseComment(model.getCanonicalName(), modelStub);
            modelStub.setParentClasspath(model.getSuperclass().getCanonicalName());
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
        try {
            // 类型+变量
            Class<?> parameterType = parameter.getType();

            int depth = 0;

            while (parameterType.isArray()) {
                parameterType = parameterType.getComponentType();
                depth++;
            }
            String typeName = parameterType.getTypeName();
            String typeSimpleName = parameterType.getSimpleName();


            ParameterStub parameterStub = new ParameterStub(
                parameter.getName(),
                fieldBuilder(parameter),
                typeSimpleName,
                parameterType.getCanonicalName()
            );
            parameterStub.setArrayDepth(depth);

            Type type;

            if (typeName.contains("$Models$")) {
                type = new ModelType();
            } else if (typeName.contains("$Enums$")) {
                type = new EnumType();
            } else if (parameterType.isAssignableFrom(Type.class)) {
                type = (Type) parameterType.newInstance();
            } else {
                type = typeMap.get(typeSimpleName);
                if (type == null) {
                    type = (Type) parameterType.newInstance();
                }
            }

            if (type == null) {
                String k = "";
            }

            parameterStub.setType(type);



            // 注解
            parseAnnotations(parameter.getAnnotations(), parameterStub);
            parseComment(fieldBuilder(parameter), parameterStub);

            return parameterStub;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseComment(String name, BaseWithAnnotationStub commentStub) {
        commentStub.setClasspath(name);
        outputStub.classHashMap.put(commentStub.getClasspath(), commentStub);
    }

    private String fieldBuilder(Field field) {
        String canonicalName = field.getDeclaringClass().getCanonicalName();
        String name = field.getName();
        return canonicalName + "." + name;
    }
}
