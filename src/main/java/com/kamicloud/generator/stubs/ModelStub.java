package com.kamicloud.generator.stubs;

import java.util.HashMap;

public class ModelStub extends BaseWithAnnotationStub {
    private HashMap<String, ParameterStub> parameters = new HashMap<>();

    public ModelStub(String name) {
        super(name);
    }

    public HashMap<String, ParameterStub> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterStub parameterStub) {
        parameters.put(parameterStub.getName(), parameterStub);
    }
}
