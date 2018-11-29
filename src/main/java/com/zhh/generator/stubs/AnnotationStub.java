package com.zhh.generator.stubs;

import java.util.HashMap;

public class AnnotationStub {
    private String name;
    private HashMap<String, String> values = new HashMap<>();

    public AnnotationStub(String name) {
        this.name = name;
        addValue("xxx", "xxx");
    }

    public String getName() {
        return name;
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public HashMap getValues() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AnnotationStub)) {
            return false;
        }
        return ((AnnotationStub) obj).getName().equals(this.name);
    }
}
