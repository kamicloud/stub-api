package com.kamicloud.generator.stubs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class TemplateStub extends BaseWithAnnotationStub {
    private ArrayList<String> constants = new ArrayList<>();
    private ArrayList<EnumStub> enums = new ArrayList<>();
    private LinkedList<ModelStub> models = new LinkedList<>();
    private ArrayList<ControllerStub> controllers = new ArrayList<>();

    private boolean current = false;

    public TemplateStub(String name) {
        super(name);
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
