package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.interfaces.CombinerInterface;
import com.kamicloud.generator.writers.components.common.FileWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ClassCombiner implements FileWriter, CombinerInterface, AddUseInterface {
    private static PHPNamespacePathTransformerInterface namespacePathTransformer;
    protected String fileName;
    private String namespace;
    private ArrayList<String> uses = new ArrayList<>();
    private String className;
    private String extend;
    private ArrayList<String> implementables = new ArrayList<>();
    private ArrayList<String> traits = new ArrayList<>();

    private ArrayList<ClassConstantCombiner> constants = new ArrayList<>();
    private ArrayList<ClassAttributeCombiner> attributes = new ArrayList<>();
    private ArrayList<ClassMethodCombiner> methods = new ArrayList<>();

    public ClassCombiner(String namespace) throws Exception {
        this(namespace, null);
    }

    public ClassCombiner(String namespace, String extend) throws Exception {
        this.extend = extend;
        this.className = getClassNameFromNamespace(namespace);
        this.namespace = getNamespaceFromFullNamespace(namespace);

        if (extend != null) {
            this.extend = addUse(extend);
        }

        if (namespacePathTransformer == null) {
            throw new Exception("no namespacePathTransformer found.");
        }
        this.fileName = namespacePathTransformer.namespaceToPath(namespace);
    }

    public static void setNamespacePathTransformer(PHPNamespacePathTransformerInterface namespacePathTransformer) {
        ClassCombiner.namespacePathTransformer = namespacePathTransformer;
    }

    public ClassCombiner addConstant(ClassConstantCombiner classConstantCombiner) {
        this.constants.add(classConstantCombiner);
        return this;
    }

    public ClassCombiner addAttribute(ClassAttributeCombiner attributeCombiner) {
        this.attributes.add(attributeCombiner);
        return this;
    }

    public ArrayList<ClassAttributeCombiner> getAttributes() {
        return attributes;
    }

    @Override
    public String addUse(String use) {
        if (!uses.contains(use)) {
            uses.add(use);
        }
        return getClassNameFromNamespace(use);
    }

    public void addTrait(String trait) {
        if (traits.contains(trait)) {
            return;
        }

        traits.add(addUse(trait));
    }

    public ClassCombiner addMethod(ClassMethodCombiner method) {
        this.methods.add(method);
        return this;
    }

    public String toString() {
        StringBuilder content = new StringBuilder();

        content.append("namespace ").append(namespace).append(";\n\n");

        HashSet<String> unqiueUses = new HashSet<>(uses);

        unqiueUses.forEach(use -> {
            if (use.equals(namespace + "\\" + className)) {
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

        traits.forEach(trait -> content.append("    use ").append(trait).append(";\n"));
        if (traits.size() > 0) {
            content.append("\n");
        }
        constants.forEach(constant -> content.append(constant.toString()).append("\n"));
        attributes.forEach(attribute -> content.append(attribute.toString()));
        if (attributes.size() > 0) {
            content.append("\n");
        }
        methods.forEach(method -> content.append(method.toString()).append("\n"));


        content.append("}\n");

        return content.toString();
    }

    public static String getNamespaceFromFullNamespace(String namespace) {
        ArrayList<String> classNamespace = new ArrayList<>();
        ArrayList<String> fullSplit = new ArrayList<>(Arrays.asList(namespace.split("\\\\")));
        for (int i = 0; i < fullSplit.size(); i++) {
            if (i != fullSplit.size() - 1) {
                classNamespace.add(fullSplit.get(i));
            }
        }
        return String.join("\\", classNamespace);
    }

    public static String getClassNameFromNamespace(String namespace) {
        String[] names = namespace.split("\\\\");
        String last = names[names.length - 1];
        return last.split(" ")[0];
    }

    public void addImplement(String implement) {
        uses.add(implement);
        implementables.add(getClassNameFromNamespace(implement));
    }

    public File getParentFile() {
        File file = getFile();
        return file.getParentFile();
    }

    public File getFile() {
        return new File(fileName);
    }

    public boolean exists() {
        return new File(fileName).exists();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void toFile() throws IOException {
        PHPFileCombiner fileCombiner = new PHPFileCombiner();

        fileCombiner.setFileName(fileName);
        fileCombiner.addBlock(this);

        fileCombiner.toFile();
    }
}
