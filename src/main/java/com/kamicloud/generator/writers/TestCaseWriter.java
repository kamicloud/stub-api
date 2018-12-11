package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.testcase.RequestStub;
import okhttp3.*;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

public class TestCaseWriter extends BaseWriter {
    private final OkHttpClient client = (new OkHttpClient.Builder()).readTimeout(Duration.ofMinutes(1)).build();

    @Override
    public void update(Observable o, Object arg) {

        Yaml yaml = new Yaml();

        SAXReader reader = new SAXReader();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(dir.getAbsolutePath() + "/TestCases.yml"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            getRequests(bufferedReader).forEach(requestStub -> {

                try {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);

                    builder.addFormDataPart("", "");
                    requestStub.getParameters().keySet().forEach(key -> {
                        String value = requestStub.getParameters().get(key);
                        builder.addFormDataPart(key, value);
                    });
                    RequestBody requestBody = builder.build();
                    Request request = new Request.Builder()
                            .url("http://localhost" + requestStub.getApi().trim())
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public class XmlHandler extends DefaultHandler {
//        public void startDocument() {
//
//        }
//    }

    private ArrayList<RequestStub> getRequests(BufferedReader bufferedReader) throws IOException {
        String line = null;
        String api = null;
        ArrayList<RequestStub> requests = new ArrayList<>();
        RequestStub requestStub = null;
        while ((line = bufferedReader.readLine()) != null) {
            ArrayList<String> splits = new ArrayList<>(Arrays.asList(line.split(":")));
            String key = splits.get(0);
            String value = String.join(":", splits.subList(1, splits.size()));
            if (line.equals("---")) {
                continue;
            }
            if (requestStub == null) {
                requestStub = new RequestStub();
            }
            if (key.equals("api")) {
                requestStub.setApi(value);
                requests.add(requestStub);
                requestStub = null;
            } else {
                requestStub.addParameter(key, value);
            }
        }
        if (requestStub == null) {
            requests.add(requestStub);
        }

        return requests;
    }

//    private boolean isApi()
}
