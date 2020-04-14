package com.kamicloud.stub.core.stubs.core;

import java.util.LinkedList;

public class ActionStub extends BaseWithAnnotationStub {
    private LinkedList<ParameterStub> requests = new LinkedList<>();
    private LinkedList<ParameterStub> responses = new LinkedList<>();

    private String uri;
    private String fullUri;

    public ActionStub(String name, String classpath) {
        super(name, classpath);
    }

    public LinkedList<ParameterStub> getRequests() {
        return requests;
    }

    public LinkedList<ParameterStub> getResponses() {
        return responses;
    }

    public void addRequest(ParameterStub parameterStub) {
        requests.add(parameterStub);
    }

    public void addResponse(ParameterStub parameterStub) {
        responses.add(parameterStub);
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
