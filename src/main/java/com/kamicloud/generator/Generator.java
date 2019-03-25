package com.kamicloud.generator;

import definitions.annotations.*;
import com.kamicloud.generator.config.ApplicationProperties;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.writers.*;
import definitions.types.CustomizeInterface;
import templates.TemplateList;
import com.sun.javadoc.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@SuppressWarnings("unchecked")
public class Generator extends Doclet {
    private final Environment env;

    private static HashMap<String, ProgramElementDoc> classDocHashMap = new HashMap<>();
    private static ArrayList<CommentInterface> classHashMap = new ArrayList<>();

    public Generator(Environment env) {
        this.env = env;
        DefaultProfileUtil.setEnv(env);
    }

    @PostConstruct
    @SuppressWarnings("unused")
    public void initApplication() {
        OutputStub output = this.parse();
        getComments();
        syncComments();

        String process = env.getProperty("process", "code");

        try {
            output.setActionUrl();
            if (process.equals("code")) {
                output.addObserver(new PostmanWriter());
                output.addObserver(new TestCaseWriter());
                output.addObserver(new DocWriter());
                output.addObserver(new LaravelWriter());
            } else if (process.equals("client")) {
                output.addObserver(new JavaClientWriter());
            } else {
                output.addObserver(new AutoTestWriter());
            }
            output.notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Generator.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.run(args);
    }

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
                        if (parameterStub.hasAnnotation(Request.name)) {
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

            baseStub.addAnnotation(annotationStub);

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

            parseComment(model.getCanonicalName(), modelStub);
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

    public void getComments() {
        String codePath = env.getProperty("generator.template-path", "./src/main/java/templates");
        File templateDir = new File(codePath + "/templates");
        File[] templateFiles = templateDir.listFiles();

        if (templateFiles == null) {
            return;
        }
        Arrays.asList(templateFiles).forEach(templateFile -> {
            if (!templateFile.getName().contains(".java")) {
                return;
            }
            com.sun.tools.javadoc.Main.execute(new String[]{
//                "-verbose",
                "-package",
                "-doclet", "com.kamicloud.generator.Generator",
                "-encoding", "utf-8",
                templateFile.getAbsolutePath()
            });
        });

    }

    public static void syncComments() {
        classHashMap.forEach((commentInterface) -> {
            ProgramElementDoc programElementDoc = classDocHashMap.get(commentInterface.getClasspath());

            if (programElementDoc != null && !programElementDoc.commentText().isEmpty()) {
                commentInterface.setComment(programElementDoc.commentText());
            }
        });
    }

    private void parseComment(String name, CommentInterface commentStub) {
        commentStub.setClasspath(name);
        classHashMap.add(commentStub);
    }

    private String fieldBuilder(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "." + field.getName();
    }

    @SuppressWarnings("unused")
    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (ClassDoc cd : classes) {
            classDocHashMap.put(cd.qualifiedTypeName(), cd);
//            System.out.println(cd.name() + "   " + cd.commentText());
            ClassDoc[] innerClasses = cd.innerClasses();
            for (ClassDoc innerClass : innerClasses) {
                Arrays.asList(innerClass.innerClasses()).forEach(classDoc -> {
//                    System.out.println("classDoc   " + classDoc.name() + "   " + classDoc.commentText());
                    classDocHashMap.put(classDoc.qualifiedTypeName(), classDoc);

                    Arrays.asList(classDoc.fields()).forEach(fieldDoc -> {
                        classDocHashMap.put(fieldDoc.qualifiedName(), fieldDoc);
//                        System.out.println("fieldDoc   " + fieldDoc.name() + "   " + fieldDoc.commentText());
                    });
                });
            }

            Arrays.asList(cd.fields()).forEach(fieldDoc -> {
                classDocHashMap.put(fieldDoc.qualifiedName(), fieldDoc);
//                        System.out.println("fieldDoc   " + fieldDoc.name() + "   " + fieldDoc.commentText());
            });
        }
        return true;
    }
}
