package com.github.kamicloud.stub.core.stubs;

import com.github.kamicloud.stub.core.utils.CommentUtil;
import com.github.kamicloud.stub.core.utils.StringUtil;
import com.github.kamicloud.stub.core.stubs.components.StringVal;
import definitions.annotations.Extendable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("unused")
public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String classpath;

    private final StringVal name;

    private BaseWithAnnotationStub parentNode;
    private final HashMap<String, AnnotationStub> annotations = new HashMap<>();
    private final ArrayList<String> comments = new ArrayList<>();
    private String comment;

    /**
     * @param name 类 / 变量名称 都将转成Upper camel
     */
    BaseWithAnnotationStub(
        StringVal name,
        String classpath
    ) {
        this.name = name;
        this.classpath = classpath;
    }

    public StringVal getName() {
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

    public void setComment(String commentContent) {
        if (commentContent == null) {
            return;
        }
        String[] comments = commentContent.split("\n");

        Arrays.asList(comments).forEach(comment -> {
            comment = comment.trim();
            if (!comment.isEmpty()) {
                this.comments.add(comment);
            }
        });

        this.comment = String.join("\n", this.comments);
    }

    public String getComment() {
        return comment;
    }

    public String getCommentTitle() {
        return CommentUtil.getTitle(comment);
    }

    public int getCommentLength() {
        if (comment == null) {
            return 0;
        }

        return comment.split("\n").length;
    }

    public boolean hasCommentBody() {
        return getCommentLength() > 1;
    }

    /**
     * 获取 注释体
     *
     * @return String
     */
    public String getCommentBody() {
        return comment;
    }

    /**
     * 注释体 lf to br
     *
     * @return String
     */
    public String getBrCommentBody() {
        return StringUtil.transformLfToBr(getCommentBody());
    }

    public void setParentNode(BaseWithAnnotationStub parentNode) {
        this.parentNode = parentNode;
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
