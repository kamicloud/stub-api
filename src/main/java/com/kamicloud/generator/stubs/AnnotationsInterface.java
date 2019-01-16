package com.kamicloud.generator.stubs;

import java.util.HashMap;

public interface AnnotationsInterface {
    void addAnnotation(AnnotationStub annotationStub);

    HashMap<String, AnnotationStub> getAnnotations();
}
