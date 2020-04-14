package com.kamicloud.stub.core.stubs.core;

import java.lang.annotation.Annotation;

public interface AnnotationsInterface {
    void addAnnotation(Annotation annotation, AnnotationStub annotationStub);

    AnnotationStub getAnnotation(Class<?> type);
}
