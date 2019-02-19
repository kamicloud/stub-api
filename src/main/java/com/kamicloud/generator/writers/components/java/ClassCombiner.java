package com.kamicloud.generator.writers.components.java;

import com.kamicloud.generator.interfaces.CombinerInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

public class ClassCombiner implements CombinerInterface {
    private static String root;
    private static String javaClasspath;
    private String fileName;
    /**
     * com.kamicloud.generator.Classname
     */
    private String classpath;
    /**
     * com.kamicloud.generator
     */
    private String packagePath;
    private ArrayList<String> uses = new ArrayList<>();
    /**
     * Classname
     */
    private String className;
    private String extend;
    private ArrayList<String> implementables = new ArrayList<>();
    private ArrayList<String> traits = new ArrayList<>();

//    private ArrayList<ClassConstantCombiner> constants = new ArrayList<>();
//    private ArrayList<ClassAttributeCombiner> attributes = new ArrayList<>();
//    private ArrayList<ClassMethodCombiner> methods = new ArrayList<>();


    public ClassCombiner(String classpath) throws Exception {
        this(classpath, null);
    }

    public ClassCombiner(String classpath, String extend) throws Exception {
        classpath = "generated." + classpath;
        this.extend = extend;
        this.className = getClassNameFromClasspath(classpath);
        this.classpath = classpath;


        String[] pieces = classpath.split("\\.");

        this.packagePath = String.join(".", pieces);
        this.fileName = String.join("/", pieces) + ".java";

        if (extend != null) {
            this.extend = addImport(extend);
        }
    }

//    public ClassCombiner addConstant(ClassConstantCombiner classConstantCombiner) {
//        this.constants.add(classConstantCombiner);
//        return this;
//    }
//
//    public ClassCombiner addAttribute(ClassAttributeCombiner attributeCombiner) {
//        this.attributes.add(attributeCombiner);
//        return this;
//    }
//
    public String addImport(String use) {
        if (!uses.contains(use)) {
            uses.add(use);
        }
        return getClassNameFromClasspath(use);
    }
//
//    public ClassCombiner addMethod(ClassMethodCombiner method) {
//        this.methods.add(method);
//        return this;
//    }
//
    @Override
    public String write() {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(classpath).append(";\n\n");

        HashSet<String> unqiueUses = new HashSet<>(uses);

        unqiueUses.forEach(use -> {
            if (use.equals(packagePath + "\\" + className)) {
                return;
            }
            content.append("use ").append(use).append(";\n");
        });
        if (unqiueUses.size() > 0) {
            content.append("\n");
        }

        content.append("class ").append(className);

        if (this.extend != null) {
            content.append(" extends ").append(this.extend);
        }
        if (implementables.size() > 0) {
            content.append(" implements ").append(String.join(", ", implementables));
        }

        content.append("\n");
        content.append("{\n");

        if (traits.size() > 0) {
            content.append("\n");
        }
//        constants.forEach(constant -> content.append(constant.write()).append("\n"));
//        attributes.forEach(attribute -> content.append(attribute.write()));
//        if (attributes.size() > 0) {
//            content.append("\n");
//        }
//        methods.forEach(method -> content.append(method.write()).append("\n"));


        content.append("}\n");

        return content.toString();
    }

    public void toFile() throws IOException {
        File file = new File(root + "/" + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

        outputStreamWriter.write(write());

        outputStreamWriter.close();
        fileOutputStream.close();
    }
//
//    public static String getNamespaceFromFullNamespace(String namespace) {
//        ArrayList<String> classNamespace = new ArrayList<>();
//        ArrayList<String> fullSplit = new ArrayList<>(Arrays.asList(namespace.split("\\\\")));
//        for (int i = 0; i < fullSplit.size(); i++) {
//            if (i != fullSplit.size() - 1) {
//                classNamespace.add(fullSplit.get(i));
//            }
//        }
//        return String.join("\\", classNamespace);
//    }
//
    public static String getClassNameFromClasspath(String namespace) {
        String[] names = namespace.split("\\.");
        String last = names[names.length - 1];
        return last.split(" ")[0];
    }

    public void addImplement(String implement) {
        uses.add(implement);
        implementables.add(getClassNameFromClasspath(implement));
    }
//
//    public File getParentFile() {
//        File file = getFile();
//        return file.getParentFile();
//    }
//
//    public File getFile() {
//        return new File(fileName);
//    }
//
//    public boolean exists() {
//        return new File(fileName).exists();
//    }
//
//    public void parse() throws Exception {
//        // FileInputStream fileInputStream = new FileInputStream(fileName);
//        // InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//
////        inputStreamReader
//    }

    private String classpathToPath(String classpath) {
        String[] pieces = classpath.split("\\.");

        return String.join("/", pieces) + ".java";
    }

    public static void setRoot(String root, String javaClasspath) {
        ClassCombiner.root = root;
        ClassCombiner.javaClasspath = javaClasspath;
    }
}
