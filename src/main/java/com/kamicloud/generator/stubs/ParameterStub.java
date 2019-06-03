package com.kamicloud.generator.stubs;

import definitions.types.Type;
import definitions.official.TypeSpec;

public class ParameterStub extends BaseWithAnnotationStub {
    private String typeSimpleName;

    private boolean array = false;

    protected int arrayDepth = 0;

    protected Type type;

    public ParameterStub(String name, String type) {
        super(name);
        this.typeSimpleName = type;
    }

    public void setArrayDepth(int depth) {
        this.arrayDepth = depth;
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public boolean isModel() {
        return type.getSpec() == TypeSpec.MODEL;
    }

    public boolean isEnum() {
        return type.getSpec() == TypeSpec.ENUM;
    }

    public boolean isBoolean() {
        return type.getSpec() == TypeSpec.BOOLEAN;
    }

    public String getTypeSimpleName() {
        return typeSimpleName;
    }

    public TypeSpec getTypeSpec() {
        return type.getSpec();
    }

    public String getTypeComment() {
        return type.getComment();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
