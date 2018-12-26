package com.kamicloud.generator.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.internal.LinkedTreeMap;
import com.kamicloud.generator.annotations.Mutable;
import com.kamicloud.generator.interfaces.PHPNamespacePathTransformerInterface;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.ParameterStub;
import com.kamicloud.generator.stubs.testcase.RequestStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.php.ClassCombiner;
import com.kamicloud.generator.writers.components.php.ClassMethodCombiner;
import okhttp3.*;
import org.springframework.core.env.Environment;

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

    private OutputStub outputStub;

    public AutoTestWriter(Environment env) {
        super(env);
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.auto-test-path")));
        testDir = new File(outputDir.getAbsolutePath() + "/tests/Generated");
    }

    @Override
    public void update(Observable o, Object arg) {
        OutputStub output = (OutputStub) o;
        outputStub = output;
        try {
            FileUtil.deleteAllFilesOfDir(testDir);

            FileInputStream fileInputStream = new FileInputStream(new File(dir.getAbsolutePath() + "/TestCases.yml"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            ClassCombiner.setNamespacePathTransformer(this);

            parseTestCases(bufferedReader);

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();

            getTestResponse(output);


        } catch (Exception e) {
            e.printStackTrace();
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
//                classCombiner.addTrait("Tests\\CreatesApplication");
                if (requests != null && !classCombiner.exists()) {
                    requests.forEach(requestStub -> {
                        try {
                            requestApi(requestStub);
                            Response response = requestStub.getResponse();
                            ClassMethodCombiner classMethodCombiner = new ClassMethodCombiner("testCase" + i);
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
                            Gson gson = new GsonBuilder()
                                    .serializeNulls()
                                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                                    .setPrettyPrinting()
                                    .create();
                            System.out.println(jsonResponse);

                            LinkedTreeMap gsonMap = (LinkedTreeMap) gson.fromJson(jsonResponse, Map.class);

//                            transformResponseMutable(gsonMap, actionStub.getResponses());

                            classMethodCombiner.addBody("$expect = <<<JSON\n" + gson.toJson(gsonMap).replace("\\", "\\\\") + "\nJSON;");
                            classMethodCombiner.addBody("$expect = json_encode(json_decode($expect));");
                            classMethodCombiner.addBody("$this->assertJsonStringEqualsJsonString($expect, $actual);");
                            classCombiner.addMethod(classMethodCombiner);


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

    private void transformResponseMutable(LinkedTreeMap response, HashMap<String, ParameterStub> parameterStubs) {
        Double status = (Double) response.get("status");
        if (response.containsKey("status")) {
            response.replace("status", status.intValue());
        }

        Object expect = response.get("data");

        if (expect instanceof LinkedTreeMap) {
            LinkedTreeMap expectLinkedTreeMap = (LinkedTreeMap) expect;
            transformParameterMutable(expectLinkedTreeMap, parameterStubs);
        }
    }

    private void transformParameterMutable(LinkedTreeMap expect, HashMap<String, ParameterStub> parameterStubs) {
        parameterStubs.forEach((parameterName, parameterStub) -> {
            String parameterType = parameterStub.getType();
            if (parameterStub.hasAnnotation(Mutable.name)) {
                expect.replace(parameterName, "*");
            } else if (parameterType.startsWith("Models.")) {
                LinkedTreeMap m = (LinkedTreeMap) expect.get(parameterName);
                if (m != null) {
                    transformParameterMutable(m, outputStub.getTemplates().get("V1").getModelByName(parameterStub.getType()).getParameters());
                }
            }
        });

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
