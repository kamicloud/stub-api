package com.kamicloud.generator.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamicloud.generator.utils.UrlUtil;
import definitions.annotations.Request;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.TemplateStub;
import com.kamicloud.generator.stubs.postman.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PostmanWriter extends BaseWriter {
    private File outputPath;
    public PostmanWriter() {
        outputPath = new File(Objects.requireNonNull(env.getProperty("generator.postman-path")) + "/API Generator.postman_collection.json");
    }

    @Override
    public void update(OutputStub output) {
        output.getTemplates().forEach((version, templateStub) -> {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                PostmanStub postmanStub = postmanOutput(version, templateStub);

                String jsonString = gson.toJson(postmanStub);

                FileOutputStream fileOutputStream = new FileOutputStream(outputPath.getAbsolutePath());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                outputStreamWriter.write(jsonString);

                outputStreamWriter.close();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private PostmanStub postmanOutput(String version, TemplateStub templateStub) {
        PostmanStub postmanStub = new PostmanStub();
        templateStub.getControllers().forEach(controller -> {
            PostmanItemStub postmanItemStub = new PostmanItemStub(controller.getName() + " " + (controller.getComment() == null ? "" : controller.getComment()));
            postmanStub.addItem(postmanItemStub);

            controller.getActions().forEach((actionName, action) -> {
                PostmanItemStub actionStub = new PostmanItemStub(actionName + " " + (action.getComment() == null ? "" : action.getComment()));
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

                postmanItemRequestUrlStub.addHost("{{host}}");
                postmanItemRequestUrlStub.addPath(UrlUtil.getUrlPrefix(true));
                postmanItemRequestUrlStub.addPath(UrlUtil.transformVersion(version));
                postmanItemRequestUrlStub.addPath(UrlUtil.transformController(controller.getName()));
                postmanItemRequestUrlStub.addPath(UrlUtil.transformAction(action.getName()));


                postmanItemRequestBodyStub.addParameter(new PostmanParameterStub("__access_token", "{{access_token}}"));

                action.getRequests().forEach((parameterName, parameter) -> {
                    if (parameter.getAnnotations().containsKey(Request.name)) {
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
