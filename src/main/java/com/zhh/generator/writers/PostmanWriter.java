package com.zhh.generator.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhh.generator.stubs.AnnotationStub;
import com.zhh.generator.stubs.OutputStub;
import com.zhh.generator.stubs.TemplateStub;
import com.zhh.generator.stubs.postman.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Observable;

public class PostmanWriter extends BaseWriter {
    @Override
    public void update(Observable o, Object arg) {
        OutputStub output = (OutputStub) o;
        output.getTemplates().forEach((version, templateStub) -> {
            try {
                File dir = new File("");
                String jsonString;
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                jsonString = gson.toJson(output);

//                System.out.println(jsonString);
                PostmanStub postmanStub = postmanOutput(templateStub);

                jsonString = gson.toJson(postmanStub);

                FileOutputStream fileOutputStream = new FileOutputStream(dir.getAbsolutePath() + "/API Generator.postman_collection.json");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                outputStreamWriter.write(jsonString);

                outputStreamWriter.close();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private PostmanStub postmanOutput(TemplateStub templateStub) {
        PostmanStub postmanStub = new PostmanStub();
        templateStub.getControllers().forEach(controller -> {
            PostmanItemStub postmanItemStub = new PostmanItemStub(controller.getName() + " " + (controller.getComment() == null ? "" : controller.getComment()));
            postmanStub.addItem(postmanItemStub);

            controller.getActions().forEach(action -> {
                PostmanItemStub actionStub = new PostmanItemStub(action.getName() + " " + (action.getComment() == null ? "" : action.getComment()));
                postmanItemStub.addItem(actionStub);

                //
                PostmanItemRequestStub postmanItemRequestStub = new PostmanItemRequestStub();
                actionStub.setRequest(postmanItemRequestStub);

                postmanItemRequestStub.setDescription(action.getComment());

                // 传输数据
                PostmanItemRequestBodyStub postmanItemRequestBodyStub = new PostmanItemRequestBodyStub();
                postmanItemRequestStub.setBody(postmanItemRequestBodyStub);

                PostmanItemRequestUrlStub postmanItemRequestUrlStub = new PostmanItemRequestUrlStub();
                postmanItemRequestStub.setUrl(postmanItemRequestUrlStub);

                postmanItemRequestUrlStub.addHost("localhost");
                postmanItemRequestUrlStub.addPath(controller.getName());
                postmanItemRequestUrlStub.addPath(action.getName());



                action.getRequests().forEach(parameter -> {
                    if (parameter.getAnnotations().contains(new AnnotationStub("Request"))) {
                        PostmanParameterStub postmanParameterStub = new PostmanParameterStub(parameter.getName());
                        String comment = parameter.getComment();
                        if (comment != null) {
                            postmanParameterStub.setDescription(comment);
                        }
                        postmanItemRequestBodyStub.addParameter(postmanParameterStub);
                    }
                });
            });
        });

        return postmanStub;
    }
}
