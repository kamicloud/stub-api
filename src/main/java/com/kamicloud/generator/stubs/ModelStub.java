package com.kamicloud.generator.stubs;

import java.util.LinkedHashMap;

public class ModelStub extends BaseWithAnnotationStub {
    private LinkedHashMap<String, ParameterStub> parameters = new LinkedHashMap<>();
    private String parentKey;
    private ModelStub parent;

    public ModelStub(String name) {
        super(name);
    }

    public LinkedHashMap<String, ParameterStub> getParameters() {
        LinkedHashMap<String, ParameterStub> parameters = new LinkedHashMap<>();
        if (parent != null) {
            parameters.putAll(parent.getParameters());
        }

        parameters.putAll(this.parameters);

        return parameters;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParent(ModelStub parent) {
        this.parent = parent;
    }

    public void addParameter(ParameterStub parameterStub) {
        parameters.put(parameterStub.getName(), parameterStub);
    }

    @Override
    public LinkedHashMap<String, ParameterStub> clone() {
        LinkedHashMap<String, ParameterStub> parameters = new LinkedHashMap<>();

        this.parameters.forEach(parameters::put);

        return parameters;
    }
}
