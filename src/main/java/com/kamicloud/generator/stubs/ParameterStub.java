package com.kamicloud.generator.stubs;

import definitions.types.TypeSpec;

public class ParameterStub extends BaseWithAnnotationStub {
    private String type;
//    private
    /** 校验规则 */
    private String rule;
    /** 类型转化的参数 */
    private String param;

    private TypeSpec spec;

    private String typeComment;

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

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public TypeSpec getSpec() {
        return spec;
    }

    public void setSpec(TypeSpec spec) {
        this.spec = spec;
    }
}
