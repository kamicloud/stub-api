package com.kamicloud.generator.stubs.annotations;

import java.util.ArrayList;

public class APIAnnotationStub {
    private ArrayList<String> methods = new ArrayList<>();

    public void addMethod(String method) {
        methods.add(method);
    }

    public ArrayList<String> getMethods() {
        return methods;
    }
}
