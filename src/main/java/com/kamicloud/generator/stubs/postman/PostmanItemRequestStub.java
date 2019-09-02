package com.kamicloud.generator.stubs.postman;

import java.util.ArrayList;

public class PostmanItemRequestStub {
    private String method = "POST";
    private ArrayList<String> header = new ArrayList<>();
    private PostmanItemRequestBodyStub body = new PostmanItemRequestBodyStub();
    private PostmanItemRequestUrlStub url = new PostmanItemRequestUrlStub();
    private String description;

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setBody(PostmanItemRequestBodyStub body) {
        this.body = body;
    }

    public PostmanItemRequestBodyStub getBody() {
        return body;
    }

    public void setUrl(PostmanItemRequestUrlStub url) {
        this.url = url;
    }

    public PostmanItemRequestUrlStub getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getHeader() {
        return header;
    }

    public void setHeader(ArrayList<String> header) {
        this.header = header;
    }
}
