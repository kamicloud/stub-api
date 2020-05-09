package com.github.kamicloud.stub.core.stubs;

import com.github.kamicloud.stub.core.stubs.components.StringVal;

import java.util.LinkedList;

public class ControllerStub extends BaseWithAnnotationStub {
    private final LinkedList<ActionStub> actions = new LinkedList<>();

    public ControllerStub(StringVal name, String classpath) {
        super(name, classpath);
    }

    public LinkedList<ActionStub> getActions() {
        return actions;
    }

    public void addAction(ActionStub actionStub) {
        actionStub.setParentNode(this);
        actions.add(actionStub);
    }
}
