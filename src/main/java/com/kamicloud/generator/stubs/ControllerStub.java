package com.kamicloud.generator.stubs;

import java.util.HashMap;

public class ControllerStub extends BaseWithAnnotationStub {
    private HashMap<String, ActionStub> actions = new HashMap<>();

    public ControllerStub(String name) {
        super(name);
    }

    public HashMap<String, ActionStub> getActions() {
        return actions;
    }

    public void addAction(ActionStub actionStub) {
        actions.put(actionStub.getName(), actionStub);
    }
}
