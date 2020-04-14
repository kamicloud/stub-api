package com.kamicloud.stub.core.stubs.core;

import definitions.types.Type;
import definitions.official.TypeSpec;

public class ParameterStub extends BaseWithAnnotationStub {
    private String typeSimpleName;

    private boolean array = false;

    protected int arrayDepth = 0;

    protected Type type;

    protected TypeStub typeStub;

    protected String typeClasspath;

    public ParameterStub(String name, String classpath, String type, String typeClasspath) {
        super(name, classpath);
        this.typeSimpleName = type;
        this.typeClasspath = typeClasspath;
    }

    public void setArrayDepth(int depth) {
        this.arrayDepth = depth;
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public TypeStub getTypeStub() {
        return typeStub;
    }

    public void setTypeStub(TypeStub typeStub) {
        this.typeStub = typeStub;
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

    @Override
    public boolean equals(Object obj) {
        return ((ParameterStub) obj).getName().equals(this.getName());
    }
}
