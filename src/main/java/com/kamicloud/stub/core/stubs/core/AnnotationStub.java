package com.kamicloud.stub.core.stubs.core;

import java.util.ArrayList;

public class AnnotationStub {
    private String name;
    private String value = "";
    private ArrayList<String> values = new ArrayList<>();

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
