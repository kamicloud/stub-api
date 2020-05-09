package com.github.kamicloud.stub.core.stubs;

import java.util.ArrayList;

public class AnnotationStub {
    private final String name;
    private String value = "";
    private final ArrayList<String> values = new ArrayList<>();

    public AnnotationStub(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getValue() {
        return value;
    }
}
