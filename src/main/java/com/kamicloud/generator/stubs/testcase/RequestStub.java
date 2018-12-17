package com.kamicloud.generator.stubs.testcase;

import okhttp3.Response;

import java.util.HashMap;

public class RequestStub {
    private String api;
    private Response response;
    private HashMap<String, String> parameters = new HashMap<>();

    public void setApi(String api) {
        this.api = api;
    }

    public String getApi() {
        return api;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
