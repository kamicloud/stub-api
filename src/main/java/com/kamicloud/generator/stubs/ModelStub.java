package com.kamicloud.generator.stubs;

import com.kamicloud.generator.interfaces.ResourceInterface;
import definitions.annotations.RESTFul;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ModelStub extends BaseWithAnnotationStub implements ResourceInterface {
    private LinkedList<ParameterStub> parameters = new LinkedList<>();
    private String parentClasspath;
    private ModelStub parent;

    private TemplateStub template;

    public ModelStub(String name, String classpath, TemplateStub template) {
        super(name, classpath);

        this.template = template;
    }

    public LinkedList<ParameterStub> getParameters() {
        LinkedList<ParameterStub> parameters = new LinkedList<>();
        if (parent != null) {
            parameters.addAll(parent.getParameters());
        }

        this.parameters.forEach(parameterStub -> {
            parameters.remove(parameterStub);
            parameters.add(parameterStub);
        });


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

    public boolean isResource() {
        return hasAnnotation(RESTFul.class);
    }

    @Override
    public LinkedList<ParameterStub> clone() {

        return new LinkedList<>(this.parameters);
    }

    public TemplateStub getTemplate() {
        return template;
    }
}
