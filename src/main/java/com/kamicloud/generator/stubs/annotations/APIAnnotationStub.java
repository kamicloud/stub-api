package com.kamicloud.generator.stubs.annotations;

import java.util.ArrayList;

public class APIAnnotationStub {
    private ArrayList<String> methods;
    private boolean transactional;

    public APIAnnotationStub(ArrayList<String> methods, boolean transactional) {
        this.methods = methods;
        this.transactional = transactional;
    }

    public void addMethod(String method) {
        methods.add(method);
    }

    public ArrayList<String> getMethods() {
        return methods;
    }
}
