package com.zhh.generator.stubs;

import java.util.ArrayList;

public class TemplateStub {
    private String version = "V1";
    private ArrayList<String> constants = new ArrayList<>();
    private ArrayList<ErrorStub> errors = new ArrayList<>();
    private ArrayList<EnumStub> enums = new ArrayList<>();
    private ArrayList<ModelStub> models = new ArrayList<>();
    private ArrayList<ControllerStub> controllers = new ArrayList<>();

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList getConstants() {
        return constants;
    }

    public void addConstant(String constant) {
        constants.add(constant);
    }

    public ArrayList<ModelStub> getModels() {
        return models;
    }

    public void addModel(ModelStub modelStub) {
        models.add(modelStub);
    }

    public void addController(ControllerStub controllerStub) {
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

    public void addError(ErrorStub errorStub) {
        errors.add(errorStub);
    }

    public ArrayList<ErrorStub> getErrors() {
        return errors;
    }
}
