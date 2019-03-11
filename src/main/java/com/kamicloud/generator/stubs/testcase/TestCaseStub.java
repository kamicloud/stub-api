package com.kamicloud.generator.stubs.testcase;

import com.google.gson.Gson;
import com.kamicloud.generator.utils.UrlUtil;
import okhttp3.Response;
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
    private LinkedHashMap<String, String> params = new LinkedHashMap<>();

    private Response response;

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

        collection.addAll(getTestCasesFromNode(file, attributes, null));

        return collection;
    }

    private static LinkedList<TestCaseStub> getTestCasesFromNode(File file, LinkedHashMap<String, Object> attributes, TestCaseStub prev) {
        if (prev == null) {
            prev = new TestCaseStub();
        }
        Object testcases = attributes.get("__testcases");
        LinkedList<TestCaseStub> collection = new LinkedList<>();

        Object enabled = attributes.get("__enabled");
        Object api = attributes.get("__api");
        Object version = attributes.get("__version");
        Object controller = attributes.get("__controller");
        Object action = attributes.get("__action");
        Object role = attributes.get("__role");
        Object user = attributes.get("__user");
        Object anchor = attributes.get("__anchor");
        Object params = attributes.get("__params");

        TestCaseStub testCaseStub = new TestCaseStub();

        testCaseStub.enabled = getBoolean(getString(enabled, prev.enabled != null ? prev.enabled.toString() : "false"));
        testCaseStub.api = getString(api, prev.api);
        testCaseStub.version = getString(version, prev.version);
        testCaseStub.controller = getString(controller, prev.controller);
        testCaseStub.action = getString(action, prev.action);
        testCaseStub.role = getString(role, prev.role);
        testCaseStub.user = getString(user, prev.user);
        testCaseStub.anchor = anchor == null ? null : anchor.toString();

        if (testCaseStub.api == null) {
            // 对于手动指定请求的接口，不使用默认的目录映射
            if (testCaseStub.version != null && testCaseStub.controller != null && testCaseStub.action != null) {
                testCaseStub.api = UrlUtil.getUrlWithPrefix(testCaseStub.version, testCaseStub.controller, testCaseStub.action);
            } else {
                List<String> paths = Arrays.asList(file.getAbsolutePath().split("[\\\\/]"));
                int size = paths.size();
                testCaseStub.action = paths.get(size - 1).replace(".yml", "");
                testCaseStub.controller = paths.get(size - 2);
                testCaseStub.version = paths.get(size - 3);
            }
        }

        if (params instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> params1 = (LinkedHashMap<String, Object>) params;
            params1.forEach((key, value) -> {
                if (value instanceof List || value instanceof Map) {
                    testCaseStub.params.put(key, gson.toJson(value));
                } else if (value != null) {
                    testCaseStub.params.put(key, value.toString());
                }
            });
        }
        // 有子样例且外部无参数时忽略外部样例
        if (params != null || testcases == null) {
            collection.add(testCaseStub);
        }

        if (testcases instanceof ArrayList<?>) {
            ((ArrayList) testcases).forEach(obj -> {
                if (obj instanceof LinkedHashMap) {
                    collection.addAll(getTestCasesFromNode(file, (LinkedHashMap<String, Object>) obj, testCaseStub));
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
        return api == null ? UrlUtil.getUrlWithPrefix(version, controller, action) : api;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public HashMap<String, String> getParameters() {
        return params;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getAnchor() {
        return anchor;
    }

    public String getRole() {
        return role;
    }

    public String getUsre() {
        return user;
    }
}
