package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.writers.CombinerInterface;

import java.util.ArrayList;

public class ClassMethodCombiner implements CombinerInterface {
    private String name;
    private String access;
    private boolean statical = false;
    private ArrayList<ClassMethodParameterCombiner> parameters = new ArrayList<>();
    private ArrayList<String> body = new ArrayList<>();

    private ClassCombiner classCombiner;

    private final String intend = "    ";

    public ClassMethodCombiner(String name) {
        this(name, "public");
    }

    public ClassMethodCombiner(String name, String access) {
        this.name = name;
        this.access = access;
    }

    public void addParameter(ClassMethodParameterCombiner parameterCombiner) {
        this.parameters.add(parameterCombiner);
    }

    public void wrapBody(ArrayList<String> header, ArrayList<String> footer) {

        ArrayList<String> body = new ArrayList<>(header);

        this.body.forEach(line -> {
            body.add(intend + line);
        });

        body.addAll(footer);

        this.body = body;
    }

    public void setBody(ArrayList<String> body) {
        this.body = new ArrayList<>();
        body.forEach(this::addBody);
    }

    public void addBody(String line) {
        body.add(line);
    }

    public ClassMethodCombiner setStatical() {
        statical = true;
        return this;
    }

    public boolean isStatical() {
        return statical;
    }

    @Override
    public String write() {
        StringBuilder content = new StringBuilder();

        content.append(intend).append(access != null ? access : "public").append(" ");
        if (statical) {
            content.append("static ");
        }
        content.append("function ").append(name).append("(");
        ArrayList<String> parameterArray = new ArrayList<>();

        parameters.forEach(parameter -> parameterArray.add(parameter.write()));
        content.append(String.join(", ", parameterArray));
        content.append(")\n");
        content.append(intend).append("{\n");
        if (body != null && body.size() > 0) {
            body.forEach(line -> {
                if (line != null) {
                    content.append(intend).append(intend).append(line).append("\n");
                }
            });
        }
        content.append(intend).append("}\n");

        return content.toString();
    }

    public void setClassCombiner(ClassCombiner classCombiner) {
        this.classCombiner = classCombiner;
    }

    public ClassCombiner getClassCombiner() {
        return classCombiner;
    }
}
