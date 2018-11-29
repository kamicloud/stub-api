package com.zhh.generator.writers;

import okhttp3.*;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

public class TestCaseWriter extends BaseWriter {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void update(Observable o, Object arg) {

        Yaml yaml = new Yaml();

        SAXReader reader = new SAXReader();
        try {
//            Document document = reader.read(dir.getAbsolutePath() + "/TestCases.yml");
//            Element root = document.getRootElement();


            FileInputStream fileInputStream = new FileInputStream(new File(dir.getAbsolutePath() + "/TestCases.yml"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                if (line.equals("---")) {

                }
                System.out.println(line);
            }

//            list.forEach(line -> {
//            yaml.loadAll(inputStream).forEach(data -> {
//                LinkedHashMap testCase = ((LinkedHashMap<String, String>) data);
//                AtomicReference<String> api = new AtomicReference<>();
//                MultipartBody.Builder builder = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM);
//                testCase.forEach((key, value) -> {
//                    if (key.equals("api")) {
//                        api.set(value.toString());
//                    } else {
//                        String valueString = value.toString();
//                        builder.addFormDataPart(key.toString(), valueString);
//                    }
//
//                });
//                RequestBody requestBody = builder.build();
//
//                Request request = new Request.Builder()
//                        .url("http://localhost/api/V1/AdminUser/GetUsers")
//                        .post(requestBody)
////                        .post(RequestBody.create(MediaType.parse("application/json"), "xxxxxx"))
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (!response.isSuccessful()) {
//                        throw new IOException("Unexpected code " + response);
//                    }
//
//                    Headers responseHeaders = response.headers();
////                    for (int i = 0; i < responseHeaders.size(); i++) {
////                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
////                    }
//
//                    System.out.println(response.body().string());
//                } catch (Exception e) {
//
//                }
//
//
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
//        try {
//            parser = SAXParserFactory.newInstance().newSAXParser();
//            XmlHandler handler = new XmlHandler();
//            parser.parse(new File("./src/contact.xml"), handler);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
//        OutputStub output = (OutputStub) o;
//        output.getTemplates().forEach((version, templateStub) -> {
//            try {
//                File dir = new File("");
//                String jsonString;
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                jsonString = gson.toJson(output);
//
//                System.out.println(jsonString);
//                PostmanStub postmanStub = postmanOutput(templateStub);
//
//                jsonString = gson.toJson(postmanStub);
//
//                FileOutputStream fileOutputStream = new FileOutputStream(dir.getAbsolutePath() + "/API Generator.postman_collection.json");
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
//                outputStreamWriter.write(jsonString);
//
//                outputStreamWriter.close();
//                fileOutputStream.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

//    public class XmlHandler extends DefaultHandler {
//        public void startDocument() {
//
//        }
//    }

    private Iterable<>
}
