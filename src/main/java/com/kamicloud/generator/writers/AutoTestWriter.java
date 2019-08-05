package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.testcase.TestCaseStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.utils.UrlUtil;
import com.kamicloud.generator.writers.components.php.ClassCombiner;
import com.kamicloud.generator.writers.components.php.ClassMethodCombiner;
import okhttp3.*;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
                LinkedList<TestCaseStub> testCaseStubs = apiMap.computeIfAbsent(testCaseStub.getApi(), k -> new LinkedList<>());

                testCaseStubs.add(testCaseStub);

                apiMap.put(testCaseStub.getApi(), testCaseStubs);
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
        output.getTemplates().forEach((version, templateStub) -> templateStub.getControllers().forEach(controllerStub -> controllerStub.getActions().forEach((actionStub) -> {
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
                            params.add("'__user' => '" + (requestStub.getUsre() == null || requestStub.getUsre().equals("null") ? "" : requestStub.getUsre()) + "',");
                            requestStub.getParameters().forEach((key, value) -> {
                                params.add("'" + key + "' => '\n" + value.replace("\\", "\\\\").replace("'", "\\'") + "\n',");
                            });
                            classMethodCombiner.setBody(params);
                            ArrayList<String> callAndAnchor = new ArrayList<>();
                            if (requestStub.getAnchor() != null && requestStub.getAnchor().equals("null")) {
                                callAndAnchor.add("# " + requestStub.getAnchor());
                            }
                            callAndAnchor.add("$response = $this->post('" + url + "', [");
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
        })));
    }

    private void requestApi(TestCaseStub requestStub) throws IOException {
        MultipartBody.Builder builder = new MultipartBody
            .Builder()
            .setType(MultipartBody.FORM);

        builder.addFormDataPart("__test_mode", "1");
        requestStub.getParameters().forEach(builder::addFormDataPart);
        if (requestStub.getRole() != null) {
            builder.addFormDataPart("__role", requestStub.getRole());
        }
        if (requestStub.getUsre() != null) {
            builder.addFormDataPart("__user", requestStub.getUsre());
        }
        RequestBody requestBody = builder.build();
        String testHost = env.getProperty("test-host", "http://localhost");
        Request request = new Request.Builder()
            .url(testHost + requestStub.getApi())
            .post(requestBody)
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
