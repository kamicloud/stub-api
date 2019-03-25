package com.kamicloud.generator.stubs;

import definitions.annotations.Extendable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String classpath;
    private String name;
    private BaseWithAnnotationStub parentNode;
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

    public void addAnnotation(Annotation type, AnnotationStub annotationStub) {
        annotations.put(type.annotationType().getName(), annotationStub);
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

    public void setParentNode(BaseWithAnnotationStub parentNode) {
        this.parentNode = parentNode;
    }

    public BaseWithAnnotationStub getParentNode() {
        return parentNode;
    }

    public Boolean hasAnnotation(Class<?> type) {
        boolean hasAnnotation = annotations.containsKey(type.getCanonicalName());
        if (type.getAnnotation(Extendable.class) != null) {
            hasAnnotation = hasAnnotation || (parentNode != null && parentNode.hasAnnotation(type));
        }
        return hasAnnotation;
    }

    public AnnotationStub getAnnotation(Class<?> type) {
        AnnotationStub annotationStub = annotations.get(type.getCanonicalName());
        if (type.getAnnotation(Extendable.class) != null && annotationStub == null && parentNode != null) {
            annotationStub = parentNode.getAnnotation(type);
        }
        return annotationStub;
    }
}
