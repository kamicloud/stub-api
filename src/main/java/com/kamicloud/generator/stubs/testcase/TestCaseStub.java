package com.kamicloud.generator.stubs.testcase;

import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class TestCaseStub {
    private String api;
    private String controller;
    private String action;
    private String role;
    private String user;
    private LinkedHashMap<String, Object> params;

    private String response;

    private static Yaml yaml = new Yaml();
    private static Gson gson = new Gson();

//    # _api: /api/V1/AdminUser/GetUsers
//    _controller: AdminUser
//    _action: GerUsers
//    _role: AdminUser
//    _user: 1
//    _testcases:
//        -
//    _role: User
//    _user: 1
//    gender: x
//    page: y
//-
//    _role: User
//    _user: 1
//    testUser: y
//    testUsers: z
//
//
//    public TestCaseStub(TestcaseApiStub testcase, String user, String role, LinkedHashMap<String, Object> params) {
//        super(role, user);
//        this.testcase = testcase;
//        this.params = params;
//    }
//
//    public String getRole() {
//        if (role == null) {
//            return testcase.getRole();
//        }
//
//        return role;
//    }
//
//    public String getUser() {
//        if (user == null) {
//            return testcase.getUser();
//        }
//
//        return user;
//    }
//
//    public LinkedHashMap<String, Object> getParams() {
//        return params;
//    }

    public static LinkedList<TestCaseStub> getTestCasesFromFile(String filename) throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(new File(".\\src\\output\\testcases\\V1\\AdminUser\\GetUsers.yml"));

        LinkedHashMap<String, Object> attributes = yaml.load(fileInputStream);


        LinkedList<TestCaseStub> collection = new LinkedList<>();

        collection.addAll(getTestCasesFromNode(attributes, null));

//        x.forEach((a,b ) -> {
//            String m = gson.toJson(b);
//            String k = "";0
//        });
//        String kk = gson.toJson(x.get("params"));

        return collection;
    }

    private static LinkedList<TestCaseStub> getTestCasesFromNode(LinkedHashMap<String, Object> attributes, TestCaseStub prev) {
        if (prev == null) {
            prev = new TestCaseStub();
        }
        Object testcases = attributes.get("_testcases");
        LinkedList<TestCaseStub> collection = new LinkedList<>();

        Object api = attributes.get("_api");
        Object controller = attributes.get("_controller");
        Object action = attributes.get("_action");
        Object role = attributes.get("_role");
        Object user = attributes.get("_user");

        TestCaseStub testCaseStub = new TestCaseStub();

        testCaseStub.api = getString(api, prev.api);
        testCaseStub.controller = getString(controller, prev.controller);
        testCaseStub.action = getString(action, prev.action);
        testCaseStub.role = getString(role, prev.role);
        testCaseStub.user = getString(user, prev.user);

        collection.add(testCaseStub);

        if (testcases instanceof ArrayList<?>) {
            ((ArrayList) testcases).forEach(obj -> {
                if (obj instanceof LinkedHashMap) {
                    collection.addAll(getTestCasesFromNode((LinkedHashMap<String, Object>) obj, testCaseStub));
                }
            });
        }

        return collection;
    }

    private static String getString(Object string, String defaultString) {
        if (string == null || string.toString().equals("null")) {
            return defaultString;
        }

        return string.toString();
    }
}
