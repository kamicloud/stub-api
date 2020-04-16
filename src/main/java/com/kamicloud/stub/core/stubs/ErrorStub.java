package com.kamicloud.stub.core.stubs;

public class ErrorStub extends BaseWithAnnotationStub {

    private String code;
    private String message;

    public ErrorStub(String name, String classpath, String code, String message) {
        super(name, classpath);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
