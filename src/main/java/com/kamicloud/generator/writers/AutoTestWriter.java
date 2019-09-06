package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.testcase.TestCaseStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.utils.StringUtil;
import com.kamicloud.generator.utils.UrlUtil;
import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.php.ClassCombiner;
import com.kamicloud.generator.writers.components.php.ClassMethodCombiner;
import okhttp3.*;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class AutoTestWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private final OkHttpClient client = (new OkHttpClient.Builder()).readTimeout(Duration.ofMinutes(1)).build();
    private HashMap<String, LinkedList<TestCaseStub>> apiMap = new HashMap<>();
    private File outputDir;
    private File root;
    private LinkedList<TestCaseStub> rawTestCases = new LinkedList<>();

    @Override
    String getName() {
        return "laravel-auto-test";
    }

    @Override
    void postConstruct() {

    }

    @Override
    void update(OutputStub output) {
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.writers.laravel-auto-test.path")));
        root = new File(env.getProperty("generator.writers.laravel-auto-test.testcases-path", ""));
        File testDir = new File(outputDir.getAbsolutePath() + "/tests/Generated");
        if (DefaultProfileUtil.isAutoTestForceReplace()) {
            FileUtil.deleteAllFilesOfDir(testDir);
        }
        try {
            ClassCombiner.setNamespacePathTransformer(this);

            getTestCases(root);
            rawTestCases.forEach(testCaseStub -> {
                if (!testCaseStub.isEnabled()) {
                    return;
                }
                LinkedList<TestCaseStub> testCaseStubs = apiMap.computeIfAbsent(testCaseStub.getPath(), k -> new LinkedList<>());

                testCaseStubs.add(testCaseStub);

                apiMap.put(testCaseStub.getPath(), testCaseStubs);
            });

            getTestResponse(output);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getTestCases(File root) throws Exception {
        if (root.isDirectory()) {
            Arrays.asList(Objects.requireNonNull(root.listFiles())).forEach(file -> {
                try {
                    getTestCases(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            LinkedList<TestCaseStub> test = TestCaseStub.getTestCasesFromFile(root);
            rawTestCases.addAll(test);
        }
    }

    private void getTestResponse(OutputStub output) {
        apiMap.forEach((path, requests) -> {
            requests.forEach(requestStub -> {
                try {
                    requestApi(requestStub);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            try {
                TestCaseStub request = requests.element();
                if (request.isResource()) {
                    FileCombiner.build(
                        outputDir.getAbsolutePath() + "/tests/Generated/" + path,
                        stringUtil.renderTemplate("laravel/testing/resource", requests)
                    );
                } else {
                    FileCombiner.build(
                        outputDir.getAbsolutePath() + "/tests/Generated/" + path,
                        stringUtil.renderTemplate("laravel/testing/transaction", requests)
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        output.getTemplates().forEach((version, templateStub) -> {
            templateStub.getModels().forEach(modelStub -> {
                if (modelStub.isResource()) {
                    try {
                        ClassCombiner classCombiner = new ClassCombiner(
                            "Tests\\Generated\\" + version + "\\RESTFul\\" + modelStub.getName() + "\\" + "IndexTest",
                            "Tests\\TestCase"
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            templateStub.getControllers().forEach(controllerStub -> controllerStub.getActions().forEach((actionStub) -> {
                String actionName = actionStub.getName();

                String url = UrlUtil.getUrlWithPrefix(version, controllerStub.getName(), actionName);

                AtomicReference<Integer> i = new AtomicReference<>(0);
                LinkedList<TestCaseStub> requests = apiMap.get(url);
                try {
                    ClassCombiner classCombiner = new ClassCombiner(
                        "Tests\\Generated\\" + version + "\\" + controllerStub.getName() + "\\" + actionStub.getName() + "Test",
                        "Tests\\TestCase"
                    );
                    classCombiner.addTrait("Illuminate\\Foundation\\Testing\\DatabaseTransactions");

                    if (requests != null && !classCombiner.exists()) {
                        requests.forEach(requestStub -> {
                            try {
                                requestApi(requestStub);
                                Response response = requestStub.getResponse();
                                ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner(classCombiner, "testCase" + i);
                                ArrayList<String> params = new ArrayList<>();
                                params.add("'__role' => '" + (requestStub.getRole() == null || requestStub.getRole().equals("null") ? "" : requestStub.getRole()) + "',");
                                params.add("'__user' => '" + (requestStub.getUser() == null || requestStub.getUser().equals("null") ? "" : requestStub.getUser()) + "',");
//                                params.add("'__test_mode' => true,");
                                requestStub.getParameters().forEach((key, value) -> {
                                    value = value.replace("\\", "\\\\").replace("'", "\\'");

                                    List valueArr = Arrays.asList(value.split("\n"));

                                    if (valueArr.isEmpty() || valueArr.size() == 1) {
                                        params.add("'" + key + "' => '" + value + "',");
                                    } else {
                                        params.add("'" + key + "' => '");
                                        valueArr.forEach(s -> {
                                            params.add("    " + s);
                                        });
                                        params.add("',");
                                    }


                                });
                                classMethodCombiner.setBody(params);
                                ArrayList<String> callAndAnchor = new ArrayList<>();
                                if (requestStub.getAnchor() != null && requestStub.getAnchor().equals("null")) {
                                    callAndAnchor.add("# " + requestStub.getAnchor());
                                }
                                callAndAnchor.add("$response = $this->" + requestStub.getMethod() + "('" + url + "', [");
                                classMethodCombiner.wrapBody(
                                    callAndAnchor,
                                    new ArrayList<>(Arrays.asList(
                                        "]);",
                                        "$actual = $response->getContent();"
                                    ))
                                );

                                String jsonResponse = Objects.requireNonNull(response.body()).string();

                                classMethodCombiner.addBody("$expect = '\n" + jsonResponse.replace("'", "\\'") + "\n';");
                                classMethodCombiner.addBody("$expect = json_encode(json_decode($expect));");
                                classMethodCombiner.addBody("$this->assertJsonStringEqualsJsonString($expect, $actual);");

                                i.getAndSet(i.get() + 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        classCombiner.toFile();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        });
    }

    private void requestApi(TestCaseStub requestStub) throws IOException {
        String method = requestStub.getHttpMethod();
        String api = requestStub.getApi();
        MultipartBody.Builder builder = new MultipartBody
            .Builder()
            .setType(MultipartBody.FORM);

        FormBody.Builder formBuilder = new FormBody.Builder();

        builder.addFormDataPart("__test_mode", "1");
        formBuilder.add("__test_mode", "1");
        requestStub.getParameters().forEach(builder::addFormDataPart);
        requestStub.getParameters().forEach(formBuilder::add);
        if (requestStub.getRole() != null) {
            builder.addFormDataPart("__role", requestStub.getRole());
            formBuilder.add("__role", requestStub.getRole());
        }
        if (requestStub.getUser() != null) {
            builder.addFormDataPart("__user", requestStub.getUser());
            formBuilder.add("__user", requestStub.getUser());
        }
        RequestBody requestBody = builder.build();
        RequestBody formBody = formBuilder.build();
        String testHost = env.getProperty("test-host", "http://localhost");

        Request request = new Request.Builder()
            .url(testHost + api + "?__test_mode=1")
            .method(method.toUpperCase(), method.equals("get") ? null : (method.equals("patch") ? formBody : requestBody))
            .build();

        Response response = client.newCall(request).execute();

        requestStub.setResponse(response);
    }

    @Override
    public String namespaceToPath(String namespace) {
        return outputDir.getAbsolutePath() + "/" + namespace.replace("Tests", "tests").replace("\\", "/") + ".php";
    }

    @Override
    public String pathToNamespace(String path) {
        return null;
    }
}
