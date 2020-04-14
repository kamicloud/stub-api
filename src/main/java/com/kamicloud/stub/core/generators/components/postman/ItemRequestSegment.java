package com.kamicloud.stub.core.generators.components.postman;

import java.util.ArrayList;

public class ItemRequestSegment {
    private String method = "POST";
    private ArrayList<String> header = new ArrayList<>();
    private ItemRequestBodySegment body = new ItemRequestBodySegment();
    private ItemRequestUrlSegment url = new ItemRequestUrlSegment();
    private String description;

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setBody(ItemRequestBodySegment body) {
        this.body = body;
    }

    public ItemRequestBodySegment getBody() {
        return body;
    }

    public void setUrl(ItemRequestUrlSegment url) {
        this.url = url;
    }

    public ItemRequestUrlSegment getUrl() {
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
