package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.utils.UrlUtil;
import org.springframework.core.env.Environment;

import java.io.*;

public class TestCaseWriter extends BaseWriter {

    private File outputDir;

    @Override
    String getName() {
        return "testcases";
    }

    @Override
    void postConstruct() {

    }

    @Override
    public void update(OutputStub output) {
        this.outputDir = new File(env.getProperty("generator.writers.testcases.path", ""));
        writeTestCases(output);
    }

    private void writeTestCases(OutputStub outputStub) {
        outputStub.getTemplates().forEach((version, template) -> {
            template.getControllers().forEach(controllerStub -> {
                controllerStub.getActions().forEach((actionStub) -> {
                    try {
                        String actionName = actionStub.getName();
                        String path = "/" + version + "/" + controllerStub.getName() + "/" + actionName;
                        String url = UrlUtil.getUrlWithPrefix(version, controllerStub.getName(), actionName);
                        File file = new File(outputDir.getAbsolutePath() + path + ".yml");
                        file.getParentFile().mkdirs();
                        if (file.exists()) {
                            return;
                        }

                        file.createNewFile();

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                        outputStreamWriter.write("# __api: /api" + url + "\n");
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
                                String requestName = requestStub.getName();
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
