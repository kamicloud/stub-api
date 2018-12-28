package com.kamicloud.generator;

import com.sun.javadoc.*;
import com.sun.tools.javadoc.Main;

import java.util.Arrays;

public class ListParams extends Doclet {
    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (int i = 0; i < classes.length; ++i) {
            ClassDoc cd = classes[i];
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

    static void printMembers(ExecutableMemberDoc[] mems) {
        for (int i = 0; i < mems.length; ++i) {
            ParamTag[] params = mems[i].paramTags();
            System.out.println(mems[i].qualifiedName());
            for (int j = 0; j < params.length; ++j) {
                System.out.println("   " + params[j].parameterName()
                        + " - " + params[j].parameterComment());
            }
        }
    }

    public static void main(final String ... args) throws Exception{
        com.sun.tools.javadoc.Main.execute(new String[] {
                "-verbose",
                "-package",
                "-doclet", "com.kamicloud.generator.ListParams",
//                "-doclet", "com.sun.javadoc.Doclet",
                "-encoding", "utf-8",
                "-classpath", "C:\\Users\\admin\\IdeaProjects\\APIGenerator\\out\\production\\classes",
                "C:\\Users\\admin\\IdeaProjects\\APIGenerator\\src\\main\\java\\com\\kamicloud\\generator\\Template.java"
        });

//        javadoc -doclet ListParams -sourcepath <source-location> java.util

    }
}