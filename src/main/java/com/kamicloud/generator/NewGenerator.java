package com.kamicloud.generator;

import com.sun.javadoc.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;


public class NewGenerator extends Doclet {
    private static HashMap<String, ClassDoc> classDocHashMap = new HashMap<>();

    public static void main(String[] args) {

        com.sun.tools.javadoc.Main.execute(new String[] {
                "-verbose",
                "-package",
                "-doclet", "com.kamicloud.generator.NewGenerator",
//                "-doclet", "com.sun.javadoc.Doclet",
                "-encoding", "utf-8",
                "-classpath", "C:\\Users\\admin\\IdeaProjects\\APIGenerator\\out\\production\\classes",
                "C:\\Users\\admin\\IdeaProjects\\APIGenerator\\src\\main\\java\\com\\kamicloud\\generator\\Template.java"
        });
        parse();
    }

    public static void parse() {

        Arrays.asList(TemplateList.templates).forEach(templateClass -> {
            Arrays.asList(templateClass.getDeclaredClasses()).forEach(element -> {
                String name = element.getName();
                String canonicalName = element.getCanonicalName();
                ClassDoc doc = classDocHashMap.get(canonicalName);
                String simpleName = element.getSimpleName();
                if (simpleName.equals("Controllers")) {
                    Arrays.asList(element.getDeclaredClasses()).forEach(controllerTemplate -> {
                        Arrays.asList(controllerTemplate.getDeclaredClasses()).forEach(actionTemplate -> {
                            Arrays.asList(actionTemplate.getDeclaredFields()).forEach(parameterStub -> {

                                Class<?> type = parameterStub.getType();
                                Boolean isMemberClass = type.isMemberClass();
                                Type types = parameterStub.getGenericType();
                                String fullTypeName = type.getTypeName();
                                String typeName = type.getSimpleName();
                                String nn = type.getName();
                                String xx = "";

                            });

                        });
                    });
                }
            });
        });
    }

    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (int i = 0; i < classes.length; ++i) {
            ClassDoc cd = classes[i];

            String rootName = cd.simpleTypeName();
            classDocHashMap.put(cd.qualifiedTypeName(), cd);
            classes[i].findClass("com.kamicloud.generator.Template");
            System.out.println(cd.name() + "   " + cd.commentText());
            ClassDoc[] innerClasses = cd.innerClasses();
            for (int j = 0; j < innerClasses.length; j++) {
                Arrays.asList(innerClasses[j].innerClasses()).forEach(classDoc -> {
                    System.out.println("classDoc   " + classDoc.name() + "   " + classDoc.commentText());

                    Arrays.asList(classDoc.fields()).forEach(fieldDoc -> {
                        System.out.println("fieldDoc   " + fieldDoc.name() + "   " + fieldDoc.commentText());
                    });
                });
            }
        }
        return true;
    }
}
