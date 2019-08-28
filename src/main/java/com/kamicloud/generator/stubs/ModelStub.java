package com.kamicloud.generator.stubs;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ModelStub extends BaseWithAnnotationStub {
    private LinkedList<ParameterStub> parameters = new LinkedList<>();
    private String parentClasspath;
    private ModelStub parent;

    public ModelStub(String name, String classpath) {
        super(name, classpath);
    }

    public LinkedList<ParameterStub> getParameters() {
        LinkedList<ParameterStub> parameters = new LinkedList<>();
        if (parent != null) {
            parent.getParameters().forEach(parameterStub -> {
                if (!parameters.contains(parameterStub)) {
                    parameters.add(parameterStub);
                }
            });
        }

        parameters.addAll(this.parameters);

        return parameters;
    }

    public void setParentClasspath(String parentClasspath) {
        this.parentClasspath = parentClasspath;
    }

    public String getParentClasspath() {
        return parentClasspath;
    }

    public void setParent(ModelStub parent) {
        this.parent = parent;
    }

    public void addParameter(ParameterStub parameterStub) {
        parameters.add(parameterStub);
    }

    @Override
    public LinkedList<ParameterStub> clone() {

        return new LinkedList<>(this.parameters);
    }
}
