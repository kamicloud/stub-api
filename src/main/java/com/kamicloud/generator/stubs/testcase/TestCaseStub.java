package com.kamicloud.generator.stubs.testcase;

import com.google.gson.Gson;
import com.kamicloud.generator.utils.StringUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("unchecked")
public class TestCaseStub {
    private Boolean enabled;
    private String api;
    private String version;
    private String controller;
    private String action;
    private String role;
    private String user;
    private String anchor;
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

    public static LinkedList<TestCaseStub> getTestCasesFromFile(File file) throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(file);

        LinkedHashMap<String, Object> attributes = yaml.load(fileInputStream);


        LinkedList<TestCaseStub> collection = new LinkedList<>();

        collection.addAll(getTestCasesFromNode(attributes, null));

        return collection;
    }

    private static LinkedList<TestCaseStub> getTestCasesFromNode(LinkedHashMap<String, Object> attributes, TestCaseStub prev) {
        if (prev == null) {
            prev = new TestCaseStub();
        }
        Object testcases = attributes.get("__testcases");
        LinkedList<TestCaseStub> collection = new LinkedList<>();

        Object enabled = attributes.get("__enabled");
        Object api = attributes.get("__api");
        Object controller = attributes.get("__controller");
        Object action = attributes.get("__action");
        Object role = attributes.get("__role");
        Object user = attributes.get("__user");
        Object anchor = attributes.get("__anchor");
        Object params = attributes.get("__params");

        TestCaseStub testCaseStub = new TestCaseStub();

        testCaseStub.enabled = getBoolean(getString(enabled, prev.enabled != null ? prev.enabled.toString() : "false"));
        testCaseStub.api = getString(api, prev.api);
        testCaseStub.controller = getString(controller, prev.controller);
        testCaseStub.action = getString(action, prev.action);
        testCaseStub.role = getString(role, prev.role);
        testCaseStub.user = getString(user, prev.user);
        testCaseStub.anchor = anchor == null ? null : anchor.toString();
        testCaseStub.params = (LinkedHashMap<String, Object>) params;

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

    private static boolean getBoolean(String s) {
        return s != null && !s.equals("") && !s.equals("0") && !s.equals("false") && !s.equals("null");
    }

    public String getApi() {
        return api == null ? "/api/" + StringUtil.transformVersion(version) + "/" + StringUtil.transformController(controller) + "/" + StringUtil.transformAction(action) : api;
    }
}
