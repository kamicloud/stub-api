package com.kamicloud.generator.stubs;

import java.util.ArrayList;

public class ModelStub extends BaseWithAnnotationStub {
    private ArrayList<ParameterStub> parameters = new ArrayList<>();

    public ModelStub(String name) {
        super(name);
    }

    public ArrayList<ParameterStub> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterStub parameterStub) {
        parameters.add(parameterStub);
    }
}
