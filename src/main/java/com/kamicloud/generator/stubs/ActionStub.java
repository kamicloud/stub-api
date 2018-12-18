package com.kamicloud.generator.stubs;

import java.util.HashMap;

public class ActionStub extends BaseWithAnnotationStub {
    private HashMap<String, ParameterStub> requests = new HashMap<>();
    private HashMap<String, ParameterStub> responses = new HashMap<>();

    private String uri;
    private String fullUri;

    public ActionStub(String name) {
        super(name);
    }


    public HashMap<String, ParameterStub> getRequests() {
        return requests;
    }

    public HashMap<String, ParameterStub> getResponses() {
        return responses;
    }

    public void addRequest(ParameterStub parameterStub) {
        requests.put(parameterStub.getName(), parameterStub);
    }

    public void addResponse(ParameterStub parameterStub) {
        responses.put(parameterStub.getName(), parameterStub);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFullUri() {
        return fullUri;
    }

    public void setFullUri(String fullUri) {
        this.fullUri = fullUri;
    }
}
