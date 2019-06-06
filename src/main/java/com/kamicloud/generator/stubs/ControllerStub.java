package com.kamicloud.generator.stubs;

import java.util.LinkedList;

public class ControllerStub extends BaseWithAnnotationStub {
    private LinkedList<ActionStub> actions = new LinkedList<>();

    public ControllerStub(String name) {
        super(name);
    }

    public LinkedList<ActionStub> getActions() {
        return actions;
    }

    public void addAction(ActionStub actionStub) {
        actionStub.setParentNode(this);
        actions.add(actionStub);
    }
}
