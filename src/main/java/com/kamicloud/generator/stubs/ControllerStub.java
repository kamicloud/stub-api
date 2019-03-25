package com.kamicloud.generator.stubs;

import java.util.LinkedHashMap;

public class ControllerStub extends BaseWithAnnotationStub {
    private LinkedHashMap<String, ActionStub> actions = new LinkedHashMap<>();

    public ControllerStub(String name) {
        super(name);
    }

    public LinkedHashMap<String, ActionStub> getActions() {
        return actions;
    }

    public void addAction(ActionStub actionStub) {
        actionStub.setParentNode(this);
        actions.put(actionStub.getName(), actionStub);
    }
}
