package com.kamicloud.generator.stubs.testcase;

public abstract class BaseTestCaseStub {
    protected String user;
    protected String role;

    public BaseTestCaseStub(String role, String user) {
        this.user = user;
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }
}
