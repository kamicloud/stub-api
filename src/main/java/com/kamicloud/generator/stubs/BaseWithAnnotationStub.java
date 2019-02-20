package com.kamicloud.generator.stubs;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String name;
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
        return comment == null ? "" : comment;
    }

    public void setExtendsFrom(String extendsFrom) {
        this.extendsFrom = extendsFrom;
    }

    public String getExtendsFrom() {
        return extendsFrom;
    }

    public Boolean hasAnnotation(String name) {
        return annotations.containsKey(name);
    }
}
