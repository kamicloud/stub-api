package com.kamicloud.generator.stubs;

import java.util.ArrayList;

public class ActionStub extends BaseWithAnnotationStub {
    private ArrayList<ParameterStub> requests = new ArrayList<>();
    private ArrayList<ParameterStub> responses = new ArrayList<>();

    public ActionStub(String name) {
        super(name);
    }


    public ArrayList<ParameterStub> getRequests() {
        return requests;
    }

    public ArrayList<ParameterStub> getResponses() {
        return responses;
    }

    public void addRequest(ParameterStub parameterStub) {
        requests.add(parameterStub);
    }

    public void addResponse(ParameterStub parameterStub) {
        responses.add(parameterStub);
    }
}
