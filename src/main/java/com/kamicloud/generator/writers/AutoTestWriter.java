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
    private File testDir;
    private File root;
    private LinkedList<TestCaseStub> rawTestCases = new LinkedList<>();

    public AutoTestWriter() {
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.auto-test-path")));
        testDir = new File(outputDir.getAbsolutePath() + "/tests/Generated");
        root = new File(env.getProperty("generator.testcases-path", ""));
        if (DefaultProfileUtil.isAutoTestForceReplace()) {
            FileUtil.deleteAllFilesOfDir(testDir);
        }
    }

    @Override
    void update(OutputStub output) {
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
            Arrays.asList(root.listFiles()).forEach(file -> {
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
        output.getTemplates().forEach((version, templateStub) -> templateStub.getControllers().forEach(controllerStub -> controllerStub.getActions().forEach((actionName, actionStub) -> {
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
                            requestStub.getParameters().forEach((key, value) -> params.add("'" + key + "' => '" + value.replace("\\", "\\\\").replace("'", "\\'") + "',"));
                            classMethodCombiner.setBody(params);
                            classMethodCombiner.wrapBody(
                                new ArrayList<>(Arrays.asList(
                                    "# " + (requestStub.getAnchor() == null ? "" : requestStub.getAnchor()),
                                    "$response = $this->post('" + url + "', ["
                                )),
                                new ArrayList<>(Arrays.asList(
                                    "]);",
                                    "$actual = $response->getContent();"
                                ))
                            );

                            String jsonResponse = Objects.requireNonNull(response.body()).string();

                            classMethodCombiner.addBody("$expect = <<<JSON\n" + jsonResponse + "\nJSON;");
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
