package com.kamicloud.generator;

import com.kamicloud.generator.interfaces.FixedEnumValueInterface;
import com.sun.javadoc.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;


public class NewGenerator extends Doclet {

//    public static void main(String[] args) {
//        String resource = TemplateList.class.getClassLoader().getResource("").toString();
//        Field[] fs = TemplateList.class.getDeclaredFields();
//        try {
//            Field f = TemplateList.class.getDeclaredField("i");
//
//            f.setAccessible(true);
//            Object ob = f.get(TemplateList.class.newInstance());
//            String  xx = "";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    //    public enum Color implements FixedEnumValueInterface {
//        red(3), blue(3), green(4), black(7), white(8);
//
//        int value;
//
//        Color(int value) {
//            this.value = value;
//        }
//
//        public int getValue() {
//            return value;
//        }
//    }
//
//    public static class RefColor {
//        public Color color;
//    }
//
//    public static void main(String[] args) {
//        try {
//            RefColor obj = new RefColor();
////            Field field = obj.getClass().getDeclaredField("color");
//
////            field.set(obj, Enum.valueOf(clazz, "red"));
//            Class clazz = Class.forName(Color.class.getName());
//            Enum e = Enum.valueOf(clazz, "red");
//            int x = 0;
//            if (e instanceof FixedEnumValueInterface) {
//                x = ((FixedEnumValueInterface) e).getValue();
//
//            }
//            System.out.println(Enum.valueOf(clazz, "red"));
//            System.out.println(((Color) e).value);
//            System.out.println(x);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    private static HashMap<String, ClassDoc> classDocHashMap = new HashMap<>();

    public static void main(String[] args) {

        new Template();
        com.sun.tools.javadoc.Main.execute(new String[] {
                "-verbose",
                "-package",
                "-doclet", "com.kamicloud.generator.NewGenerator",
//                "-doclet", "com.sun.javadoc.Doclet",
                "-encoding", "utf-8",
                "-classpath", "C:\\Users\\admin\\IdeaProjects\\APIGenerator\\out\\production\\classes",
//                Template.class.getClassLoader()
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
                if (simpleName.equals("Enums")) {

                    Arrays.asList(element.getDeclaredClasses()).forEach(controllerTemplate -> {
                        Arrays.asList(controllerTemplate.getDeclaredFields()).forEach(field -> {
                            String xx = field.getName();
//                            Enum.valueOf(controllerTemplate.getClass(), field.getName());
                            String n = "x";
                        });
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
