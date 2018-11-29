package com.zhh.generator.stubs;

public class ParameterStub extends BaseWithAnnotationStub {
    private String type;

    public ParameterStub(String name, String type) {
        super(name);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
