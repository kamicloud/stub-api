package com.kamicloud.stub.core.stubs;

import com.kamicloud.stub.core.stubs.components.StringVal;

import java.util.ArrayList;
import java.util.LinkedList;

public class TemplateStub extends BaseWithAnnotationStub {
    private final ArrayList<String> constants = new ArrayList<>();
    private final ArrayList<EnumStub> enums = new ArrayList<>();
    private final LinkedList<ModelStub> models = new LinkedList<>();
    private final ArrayList<ControllerStub> controllers = new ArrayList<>();

    private boolean current = false;

    public TemplateStub(StringVal name, String classpath) {
        super(name, classpath);
    }

    public ArrayList<String> getConstants() {
        return constants;
    }

    public void addConstant(String constant) {
        constants.add(constant);
    }

    public LinkedList<ModelStub> getModels() {
        return models;
    }

    public void addModel(ModelStub modelStub) {
        models.add(modelStub);
    }

    public void addController(ControllerStub controllerStub) {
        controllerStub.setParentNode(this);
        controllers.add(controllerStub);
    }

    public ArrayList<ControllerStub> getControllers() {
        return controllers;
    }

    public void addEnum(EnumStub enumStub) {
        enums.add(enumStub);
    }

    public ArrayList<EnumStub> getEnums() {
        return enums;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
