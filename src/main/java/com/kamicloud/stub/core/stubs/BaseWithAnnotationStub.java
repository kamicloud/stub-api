package com.kamicloud.stub.core.stubs;

import com.google.common.base.CaseFormat;
import com.kamicloud.stub.core.stubs.components.StringVal;
import com.kamicloud.stub.core.utils.CommentUtil;
import com.kamicloud.stub.core.utils.StringUtil;
import definitions.annotations.Extendable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("unused")
public class BaseWithAnnotationStub implements AnnotationsInterface, CommentInterface {
    private String classpath;

    private StringVal name;
    private String upperCamelName;
    private String lowerCamelName;
    private String lowerUnderScoreName;
    private String upperUnderScoreName;

    private BaseWithAnnotationStub parentNode;
    private HashMap<String, AnnotationStub> annotations = new HashMap<>();
    private ArrayList<String> comments = new ArrayList<>();
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

        this.upperCamelName = name.getUPPER_CAMEL();
        this.lowerCamelName = name.getLOWER_CAMEL();
        this.lowerUnderScoreName = name.getLOWER_UNDERSCORE();
        this.upperUnderScoreName = name.getUPPER_UNDERSCORE();

    }

    public StringVal getName() {
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
