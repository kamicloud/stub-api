package com.kamicloud.generator.stubs.testcase;

public class TestcaseApiStub extends BaseTestCaseStub {
    private String api;

    public TestcaseApiStub(String api, String role, String user) {
        super(role, user);

        this.api = api;
    }

    public String getApi() {
        return api;
    }
}
