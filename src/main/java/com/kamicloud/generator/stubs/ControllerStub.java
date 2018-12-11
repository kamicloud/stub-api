package com.kamicloud.generator.stubs;

import java.util.ArrayList;

public class ControllerStub extends BaseWithAnnotationStub {
    private ArrayList<ActionStub> actions = new ArrayList<>();

    public ControllerStub(String name) {
        super(name);
    }

    public ArrayList<ActionStub> getActions() {
        return actions;
    }

    public void addAction(ActionStub actionStub) {
        actions.add(actionStub);
    }
}
