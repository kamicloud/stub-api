package com.kamicloud.stub.core.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamicloud.stub.core.utils.UrlUtil;
import com.kamicloud.stub.core.generators.components.postman.*;
import definitions.annotations.Request;
import com.kamicloud.stub.core.stubs.core.OutputStub;
import com.kamicloud.stub.core.stubs.core.TemplateStub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class PostmanGenerator extends BaseGenerator {
    private File outputPath;

    @Override
    public void postConstruct() {
        outputPath = new File(config.getGenerators().getPostman().getPath() + "/API Generator.postman_collection.json");
    }

    @Override
    public void render(OutputStub output) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            MainSegment mainSegment = postmanOutput(output);

            output.getTemplates().forEach((version, templateStub) -> {
            });

            String jsonString = gson.toJson(mainSegment);

            FileOutputStream fileOutputStream = new FileOutputStream(outputPath.getAbsolutePath());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(jsonString);

            outputStreamWriter.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MainSegment postmanOutput(OutputStub outputStub) {
        MainSegment mainSegment = new MainSegment();

        outputStub.getTemplates().forEach((version, templateStub) -> {
            ItemSegment templateOutput = new ItemSegment(version);
            mainSegment.addItem(templateOutput);

            templateOutput(templateOutput, version, templateStub);
        });

        return mainSegment;
    }

    private void templateOutput(ItemSegment postmanStub, String version, TemplateStub templateStub) {
        ItemSegment restfulItemStub = new ItemSegment("RESTFul");

        templateStub.getModels().forEach(modelStub -> {
            if (modelStub.isResource()) {
                ItemSegment restfulOne = new ItemSegment(modelStub.getName() + " " + (modelStub.getComment() == null ? "" : modelStub.getComment()));

                restfulItemStub.addItem(restfulOne);

                ItemSegment indexStub = new ItemSegment("Index");
                ItemSegment storeStub = new ItemSegment("Store");
                ItemSegment updateStub = new ItemSegment("Update");
                ItemSegment showStub = new ItemSegment("Show");
                ItemSegment destroyStub = new ItemSegment("Destroy");

                restfulOne.addItem(indexStub);
                restfulOne.addItem(storeStub);
                restfulOne.addItem(updateStub);
                restfulOne.addItem(showStub);
                restfulOne.addItem(destroyStub);

                ItemRequestSegment indexRequest = new ItemRequestSegment();
                ItemRequestSegment storeRequest = new ItemRequestSegment();
                ItemRequestSegment updateRequest = new ItemRequestSegment();
                ItemRequestSegment showRequest = new ItemRequestSegment();
                ItemRequestSegment destroyRequest = new ItemRequestSegment();

                indexStub.setRequest(indexRequest);
                storeStub.setRequest(storeRequest);
                updateStub.setRequest(updateRequest);
                showStub.setRequest(showRequest);
                destroyStub.setRequest(destroyRequest);

                indexRequest.setMethod("GET");
                indexRequest.setUrl(new ItemRequestUrlSegment(new String[]{
                    "{{host}}"
                }, new String[]{
                    UrlUtil.getUrlPrefix(true),
                    UrlUtil.transformVersion(version),
                    "restful",
                    UrlUtil.transformAction(modelStub.getName())
                }));

                storeRequest.setMethod("PUT");
                storeRequest.setUrl(new ItemRequestUrlSegment(new String[]{
                    "{{host}}"
                }, new String[]{
                    UrlUtil.getUrlPrefix(true),
                    UrlUtil.transformVersion(version),
                    "restful",
                    UrlUtil.transformAction(modelStub.getName())
                }));

                showRequest.setMethod("GET");
                showRequest.setUrl(new ItemRequestUrlSegment(new String[]{
                    "{{host}}"
                }, new String[]{
                    UrlUtil.getUrlPrefix(true),
                    UrlUtil.transformVersion(version),
                    "restful",
                    UrlUtil.transformAction(modelStub.getName()),
                    "1"
                }));

                updateRequest.setMethod("PATCH");
                updateRequest.setUrl(new ItemRequestUrlSegment(new String[]{
                    "{{host}}"
                }, new String[]{
                    UrlUtil.getUrlPrefix(true),
                    UrlUtil.transformVersion(version),
                    "restful",
                    UrlUtil.transformAction(modelStub.getName()),
                    "1"
                }));

                destroyRequest.setMethod("DELETE");
                destroyRequest.setUrl(new ItemRequestUrlSegment(new String[]{
                    "{{host}}"
                }, new String[]{
                    UrlUtil.getUrlPrefix(true),
                    UrlUtil.transformVersion(version),
                    "restful",
                    UrlUtil.transformAction(modelStub.getName()),
                    "1"
                }));


            }
        });

        postmanStub.addItem(restfulItemStub);

        templateStub.getControllers().forEach(controller -> {
            ItemSegment itemSegment = new ItemSegment(controller.getName() + " " + (controller.getComment() == null ? "" : controller.getComment()));
            postmanStub.addItem(itemSegment);

            controller.getActions().forEach((action) -> {
                String actionName = action.getName();

                ItemSegment actionStub = new ItemSegment(actionName + " " + (action.getComment() == null ? "" : action.getComment()));
                itemSegment.addItem(actionStub);

                //
                ItemRequestSegment itemRequestSegment = new ItemRequestSegment();
                actionStub.setRequest(itemRequestSegment);

                itemRequestSegment.setDescription(action.getComment());

                // 传输数据
                ItemRequestBodySegment itemRequestBodySegment = new ItemRequestBodySegment();
                itemRequestSegment.setBody(itemRequestBodySegment);

                ItemRequestUrlSegment itemRequestUrlSegment = new ItemRequestUrlSegment();
                itemRequestSegment.setUrl(itemRequestUrlSegment);

                itemRequestUrlSegment.addHost("{{host}}");
                itemRequestUrlSegment.addPath(UrlUtil.getUrlPrefix(true));
                itemRequestUrlSegment.addPath(UrlUtil.transformVersion(version));
                itemRequestUrlSegment.addPath(UrlUtil.transformController(controller.getName()));
                itemRequestUrlSegment.addPath(UrlUtil.transformAction(action.getName()));

                itemRequestBodySegment.addParameter(new ParameterSegment("__test_mode", "1"));
                itemRequestBodySegment.addParameter(new ParameterSegment("__user", ""));

                action.getRequests().forEach((parameter) -> {
                    if (parameter.hasAnnotation(Request.class)) {
                        ParameterSegment parameterSegment = new ParameterSegment(parameter.getName());
                        String comment = parameter.getComment();
                        if (comment != null) {
                            parameterSegment.setDescription(comment);
                        }
                        itemRequestBodySegment.addParameter(parameterSegment);
                    }
                });
            });
        });
    }
}
