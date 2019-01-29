package com.kamicloud.generator.stubs.testcase;

import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

public class TestcaseStub extends BaseTestCaseStub {
    private String api;

    private ArrayList<TestCaseParameterStub> testcases;

    public TestcaseStub(String api) {
        this.api = api;
    }

//    public void setApi(String api) {
//        this.api = api;
//    }

    public String getApi() {
        return api;
    }
}
