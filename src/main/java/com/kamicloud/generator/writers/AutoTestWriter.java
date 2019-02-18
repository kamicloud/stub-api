package com.kamicloud.generator.writers;

import com.google.gson.Gson;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.testcase.RequestStub;
import com.kamicloud.generator.stubs.testcase.TestCaseStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.php.ClassCombiner;
import com.kamicloud.generator.writers.components.php.ClassMethodCombiner;
import okhttp3.*;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class AutoTestWriter extends BaseWriter implements PHPNamespacePathTransformerInterface {
    private final OkHttpClient client = (new OkHttpClient.Builder()).readTimeout(Duration.ofMinutes(1)).build();
    private HashMap<String, ArrayList<RequestStub>> apiMap = new HashMap<>();
    private File outputDir;
    private File testDir;

    public AutoTestWriter(Environment env) {
        super(env);
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.auto-test-path")));
        testDir = new File(outputDir.getAbsolutePath() + "/tests/Generated");
    }

    @Override
    public void update(Observable o, Object arg) {
        OutputStub output = (OutputStub) o;
        try {
            FileUtil.deleteAllFilesOfDir(testDir);

            ClassCombiner.setNamespacePathTransformer(this);

            File root = new File(env.getProperty("generator.testcases-path", ""));

            getTestCases(root);

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
            FileInputStream fileInputStream = new FileInputStream(root);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            parseTestCases(bufferedReader);
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        }
    }

    private void getTestResponse(OutputStub output) {
        output.getTemplates().forEach((version, templateStub) -> templateStub.getControllers().forEach(controllerStub -> controllerStub.getActions().forEach((actionName, actionStub) -> {
            String url = String.join(
                "/",
                "",
                "api",
                version,
                controllerStub.getName(),
                actionName
            );

            AtomicReference<Integer> i = new AtomicReference<>(0);
            ArrayList<RequestStub> requests = apiMap.get(url);
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
                                "$response = $this->post('" + url + "', [",
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

    private void requestApi(RequestStub requestStub) throws IOException {
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

    private void parseTestCases(BufferedReader bufferedReader) throws IOException {
        String line;
        String api = null;
        RequestStub requestStub = null;

//        Yaml yaml = new Yaml();
//        FileInputStream in = new FileInputStream(new File(".\\src\\output\\testcases\\V1\\AdminUser\\GetUsers.yml"));
//        Gson gson = new Gson();
//        LinkedHashMap x = yaml.load(bufferedReader);
//        LinkedList<TestCaseStub> test = TestCaseStub.getTestCasesFromFile("");
//        x.forEach((a,b ) -> {
//            String m = gson.toJson(b);
//            String k = "";
//        });
//        String kk = gson.toJson(x.get("params"));
        while ((line = bufferedReader.readLine()) != null) {
            ArrayList<String> splits = new ArrayList<>(Arrays.asList(line.split(":")));
            String key = splits.get(0);
            api = String.join(":", splits.subList(1, splits.size())).trim();
            if (line.equals("---")) {
                requestStub = null;
                continue;
            }
            if (requestStub == null) {
                requestStub = new RequestStub();
            }
            if (key.equals("api")) {
                requestStub.setApi(api);
                getRequestsByApi(api).add(requestStub);
            } else if (!key.equals("")) {
                requestStub.addParameter(key, api);
            }
        }
        if (requestStub != null) {
            getRequestsByApi(api).add(requestStub);
        }

    }

    private ArrayList<RequestStub> getRequestsByApi(String api) {
        return apiMap.computeIfAbsent(api, k -> new ArrayList<>());
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
