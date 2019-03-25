package com.kamicloud.generator.stubs;

import definitions.annotations.DBField;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String classpath;
    private String name;
    private BaseWithAnnotationStub parent;
    private HashMap<String, AnnotationStub> annotations = new HashMap<>();
    private ArrayList<String> comments = new ArrayList<>();
    private String comment;
    private String extendsFrom;

    BaseWithAnnotationStub(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, AnnotationStub> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(AnnotationStub annotationStub) {
        annotations.put(annotationStub.getName(), annotationStub);
    }

    @Override
    public String getClasspath() {
        return classpath;
    }

    @Override
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public void setComment(String comment) {
        String[] comments = comment.split("\n");

        for (int i = 0; i < comments.length; i++) {
            comments[i] = comments[i].trim();
            if (!comments[i].isEmpty()) {
                this.comments.add(comments[i]);
            }
        }
        this.comment = String.join("\n", this.comments);
    }

    public String getComment() {
        return comment;
    }

    public void setExtendsFrom(String extendsFrom) {
        this.extendsFrom = extendsFrom;
    }

    public String getExtendsFrom() {
        return extendsFrom;
    }

    public void setParent(BaseWithAnnotationStub parent) {
        this.parent = parent;
    }

    public BaseWithAnnotationStub getParent() {
        return parent;
    }

    public Boolean hasAnnotation(String name) {
        return annotations.containsKey(name) || (parent != null && parent.hasAnnotation(name));
    }

    public AnnotationStub getAnnotation(String name) {
        AnnotationStub annotationStub = annotations.get(name);
        if (annotationStub == null && parent != null) {
            annotationStub = parent.getAnnotation(name);
        }
        return annotationStub;
    }
}
