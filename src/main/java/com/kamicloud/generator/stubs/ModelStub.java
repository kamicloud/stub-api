package com.kamicloud.generator.stubs;

import java.util.LinkedHashMap;

public class ModelStub extends BaseWithAnnotationStub {
    private LinkedHashMap<String, ParameterStub> parameters = new LinkedHashMap<>();

    public ModelStub(String name) {
        super(name);
    }

    public LinkedHashMap<String, ParameterStub> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterStub parameterStub) {
        parameters.put(parameterStub.getName(), parameterStub);
    }
}
