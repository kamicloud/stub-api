package com.zhh.generator.stubs.postman;

import java.util.ArrayList;

public class PostmanItemRequestBodyStub {
    // urlencoded
    private String mode = "formdata";
    private ArrayList<PostmanParameterStub> formdata = new ArrayList<>();
    private ArrayList<PostmanParameterStub> urlencoded = new ArrayList<>();

    public String getMode() {
        return mode;
    }

    public ArrayList<PostmanParameterStub> getFormdata() {
        return formdata;
    }

    public ArrayList<PostmanParameterStub> getUrlencoded() {
        return urlencoded;
    }

    public void addParameter(PostmanParameterStub parameter) {
        this.formdata.add(parameter);
        this.urlencoded.add(parameter);
    }
}
