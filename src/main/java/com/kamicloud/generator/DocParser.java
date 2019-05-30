package com.kamicloud.generator;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("unused")
public class DocParser extends Doclet {
    static HashMap<String, ProgramElementDoc> classDocHashMap = new HashMap<>();

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
