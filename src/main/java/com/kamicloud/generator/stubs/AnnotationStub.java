package com.kamicloud.generator.stubs;

import definitions.annotations.MethodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AnnotationStub {
    private String name;
    private HashMap<String, Object> values = new HashMap<>();

    public AnnotationStub(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addValue(String key, Object value) {
        values.put(key, value);
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AnnotationStub)) {
            return false;
        }
        return ((AnnotationStub) obj).getName().equals(this.name);
    }

    public ArrayList<String> getAPIMethods() {
        ArrayList<String> methodResults = new ArrayList<>();
        MethodType[] methodTypes = (MethodType[]) values.get("methods");
        Arrays.asList(methodTypes).forEach(method -> {
            String methodName = method.toString();
            methodResults.add(methodName);
        });

        return methodResults;
    }
}
