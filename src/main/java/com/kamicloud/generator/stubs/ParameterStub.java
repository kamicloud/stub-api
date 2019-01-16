package com.kamicloud.generator.stubs;

public class ParameterStub extends BaseWithAnnotationStub {
    private String type;

    private boolean array = false;
    private boolean model = false;
    private boolean enumeration = false;

    public ParameterStub(String name, String type) {
        super(name);
        this.type = type;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public void setModel(boolean model) {
        this.model = model;
    }

    public void setEnum(boolean enumeration) {
        this.enumeration = enumeration;
    }

    public boolean isArray() {
        return array;
    }

    public boolean isModel() {
        return model;
    }

    public boolean isEnum() {
        return enumeration;
    }

    public String getType() {
        return type;
    }
}
