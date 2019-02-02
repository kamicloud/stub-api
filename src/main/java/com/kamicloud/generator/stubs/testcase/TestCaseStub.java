package com.kamicloud.generator.stubs.testcase;

import java.util.LinkedHashMap;

public class TestCaseStub extends BaseTestCaseStub {
    private TestcaseApiStub testcase;
    private LinkedHashMap<String, Object> params;

    public TestCaseStub(TestcaseApiStub testcase, String user, String role, LinkedHashMap<String, Object> params) {
        super(role, user);
        this.testcase = testcase;
        this.params = params;
    }

    public String getRole() {
        if (role == null) {
            return testcase.getRole();
        }

        return role;
    }

    public String getUser() {
        if (user == null) {
            return testcase.getUser();
        }

        return user;
    }

    public LinkedHashMap getParams() {
        return params;
    }
}
