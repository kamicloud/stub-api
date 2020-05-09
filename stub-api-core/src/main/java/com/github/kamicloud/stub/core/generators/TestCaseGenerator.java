package com.github.kamicloud.stub.core.generators;

import com.github.kamicloud.stub.core.stubs.OutputStub;
import com.github.kamicloud.stub.core.utils.UrlUtil;

import java.io.*;

public class TestCaseGenerator extends BaseGenerator {

    private File outputDir;

    @Override
    public void postConstruct() {

    }

    @Override
    public void render(OutputStub output) {
        this.outputDir = new File(config.getGenerators().getTestcases().getPath());
        writeTestCases(output);
    }

    private void writeTestCases(OutputStub outputStub) {
        outputStub.getTemplates().forEach((version, template) -> {
            template.getControllers().forEach(controllerStub -> {
                controllerStub.getActions().forEach((actionStub) -> {
                    try {
                        String actionName = actionStub.getName().toString();
                        String path = "/" + version + "/" + controllerStub.getName() + "/" + actionName;
                        String url = UrlUtil.getUrlWithPrefix(version, controllerStub.getName().toString(), actionName);
                        File file = new File(outputDir.getAbsolutePath() + path + ".yml");
                        file.getParentFile().mkdirs();
                        if (file.exists()) {
                            return;
                        }

                        file.createNewFile();

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                        outputStreamWriter.write("# __api: " + url + "\n");
                        outputStreamWriter.write("__enabled: false\n");
//                        outputStreamWriter.write("# __version:\n");
//                        outputStreamWriter.write("__controller: " + controllerStub.getName() + "\n");
//                        outputStreamWriter.write("__action: " + actionName + "\n");
                        outputStreamWriter.write("__role:\n");
                        outputStreamWriter.write("__user:\n");
                        outputStreamWriter.write("__anchor:\n");
                        outputStreamWriter.write("__params:\n");

                        actionStub.getRequests().forEach((requestStub) -> {
                            try {
                                String requestName = requestStub.getName().toString();
                                outputStreamWriter.write("  " + requestName + ":\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        outputStreamWriter.write("# __testcases:\n");
                        outputStreamWriter.write("\n");


                        outputStreamWriter.close();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }
}
