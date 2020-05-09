package com.github.kamicloud.stub.core.segments.postman;

import java.util.ArrayList;

public class ItemRequestBodySegment {
    // urlencoded
    private String mode = "formdata";
    private ArrayList<ParameterSegment> formdata = new ArrayList<>();
    private ArrayList<ParameterSegment> urlencoded = new ArrayList<>();

    public String getMode() {
        return mode;
    }

    public ArrayList<ParameterSegment> getFormdata() {
        return formdata;
    }

    public ArrayList<ParameterSegment> getUrlencoded() {
        return urlencoded;
    }

    public void addParameter(ParameterSegment parameter) {
        this.formdata.add(parameter);
        this.urlencoded.add(parameter);
    }
}
