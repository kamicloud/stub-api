package com.kamicloud.generator.stubs;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.utils.CommentUtil;
import com.kamicloud.generator.utils.StringUtil;
import definitions.annotations.Extendable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String classpath;

    private String name;
    private String upperCamelName;
    private String lowerCamelName;
    private String lowerUnderScoreName;
    private String upperUnderScoreName;

    private BaseWithAnnotationStub parentNode;
    private HashMap<String, AnnotationStub> annotations = new HashMap<>();
    private ArrayList<String> comments = new ArrayList<>();
    private String comment;

    private String dtoFolder = DefaultProfileUtil.getEnv().getProperty("generator.writers.laravel.dto-folder", "DTOs");
    private String dtoSuffix = DefaultProfileUtil.getEnv().getProperty("generator.writers.laravel.dto-suffix", "DTO");

    /**
     * @param name 类 / 变量名称 都将转成Upper camel
     */
    BaseWithAnnotationStub(
        String name,
        String classpath
    ) {
        this.name = name;
        this.classpath = classpath;

        this.upperCamelName = name;
        this.lowerCamelName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
        this.lowerUnderScoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        this.upperUnderScoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);

    }

    public String getName() {
        return name;
    }

    public String getUpperCamelName() {
        return upperCamelName;
    }

    public String getLowerCamelName() {
        return lowerCamelName;
    }

    public String getLowerUnderScoreName() {
        return lowerUnderScoreName;
    }

    public String getUpperUnderScoreName() {
        return upperUnderScoreName;
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
        if (comment == null) {
            return;
        }
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

    public String getDtoFolder() {
        return dtoFolder;
    }

    public String getDtoSuffix() {
        return dtoSuffix;
    }
}
