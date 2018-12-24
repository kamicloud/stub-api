package com.kamicloud.generator.stubs;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ActionStub extends BaseWithAnnotationStub {
    private LinkedHashMap<String, ParameterStub> requests = new LinkedHashMap<>();
    private LinkedHashMap<String, ParameterStub> responses = new LinkedHashMap<>();

    private String uri;
    private String fullUri;

    public ActionStub(String name) {
        super(name);
    }


    public LinkedHashMap<String, ParameterStub> getRequests() {
        return requests;
    }

    public LinkedHashMap<String, ParameterStub> getResponses() {
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
