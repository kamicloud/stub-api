package com.kamicloud.generator.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TemplateStub extends BaseWithAnnotationStub {
    private ArrayList<String> constants = new ArrayList<>();
    private ArrayList<ErrorStub> errors = new ArrayList<>();
    private ArrayList<EnumStub> enums = new ArrayList<>();
    private HashMap<String, ModelStub> models = new HashMap<>();
    private ArrayList<ControllerStub> controllers = new ArrayList<>();

    public TemplateStub(String name) {
        super(name);
    }

    public ArrayList<String> getConstants() {
        return constants;
    }

    public void addConstant(String constant) {
        constants.add(constant);
    }

    public HashMap<String, ModelStub> getModels() {
        return models;
    }

    public void addModel(ModelStub modelStub) {
        models.put(modelStub.getName(), modelStub);
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

    public ModelStub getModelByName(String name) {
        String realname = name.replace("Models.", "");
        AtomicReference<ModelStub> model = new AtomicReference<>();
        models.forEach((modelName, modelStub) -> {
            if (modelStub.getName().equals(realname)) {
                model.set(modelStub);
            }
        });

        return model.get();
    }
}
