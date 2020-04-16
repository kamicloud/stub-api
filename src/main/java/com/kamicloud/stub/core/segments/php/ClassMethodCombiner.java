package com.kamicloud.stub.core.segments.php;

import com.kamicloud.stub.core.interfaces.CombinerInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("unused")
public class ClassMethodCombiner implements CombinerInterface, AddUseInterface {
    private String name;
    private String access;
    private boolean statical = false;
    private ArrayList<ClassMethodParameterCombiner> parameters = new ArrayList<>();
    private ArrayList<String> body = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();

    private ClassCombiner classCombiner;

    private final String intend = "    ";

    public ClassMethodCombiner(ClassCombiner classCombiner, String name) {
        this(classCombiner, name, "public");
    }

    public ClassMethodCombiner(ClassCombiner classCombiner, String name, String access) {
        this.name = name;
        this.access = access;
        this.classCombiner = classCombiner;

        classCombiner.addMethod(this);
    }

    public void addParameter(ClassMethodParameterCombiner parameterCombiner) {
        this.parameters.add(parameterCombiner);
    }

    public void wrapBody(String[] header, String[] footer) {
        wrapBody(new ArrayList<>(Arrays.asList(header)), new ArrayList<>(Arrays.asList(footer)));
    }

    public void wrapBody(String header, String footer) {
        wrapBody(header, new ArrayList<>(Collections.singleton(footer)));
    }

    public void wrapBody(String header, ArrayList<String> footer) {
        wrapBody(new ArrayList<>(Collections.singletonList(header)), footer);
    }

    public void wrapBody(ArrayList<String> header, String footer) {
        wrapBody(header, new ArrayList<>(Collections.singleton(footer)));
    }

    public void wrapBody(ArrayList<String> header, ArrayList<String> footer) {
        ArrayList<String> body = new ArrayList<>(header);

        this.body.forEach(line -> {
            body.add(intend + line);
        });

        body.addAll(footer);

        this.body = body;
    }

    public ClassMethodCombiner setBody(String ...body) {
        setBody(new ArrayList<>(Arrays.asList(body)));

        return this;
    }

    public ClassMethodCombiner setBody(ArrayList<String> body) {
        this.body = new ArrayList<>();
        body.forEach(this::addBody);

        return this;
    }

    public void addBody(String line) {
        body.add(line);
    }

    public void addBody(String ...lines) {
        for (String line : lines) {
            addBody(line);
        }
    }

    public ClassMethodCombiner setStatical() {
        statical = true;
        return this;
    }

    public boolean isStatical() {
        return statical;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();

        if (comments.size() > 0) {
            content.append("    /**\n");
            comments.forEach(comment -> content.append("     * ").append(comment).append("\n"));
            content.append("     */\n");
        }
        content.append(intend).append(access != null ? access : "public").append(" ");
        if (statical) {
            content.append("static ");
        }
        content.append("function ").append(name).append("(");
        ArrayList<String> parameterArray = new ArrayList<>();

        parameters.forEach(parameter -> parameterArray.add(parameter.toString()));
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

    public ClassCombiner getClassCombiner() {
        return classCombiner;
    }

    @Override
    public String addUse(String use) {
        return this.classCombiner.addUse(use);
    }

    public void addComment(String comment) {
        if (comment != null) {
            this.comments.addAll(Arrays.asList(comment.split("\n")));
        }
    }

    public static ClassMethodCombiner build(ClassCombiner classCombiner, String name, String access) {
        return new ClassMethodCombiner(classCombiner, name, access);
    }
}
