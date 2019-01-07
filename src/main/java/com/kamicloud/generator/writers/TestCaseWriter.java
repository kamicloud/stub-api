package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.testcase.RequestStub;
import okhttp3.*;
import org.dom4j.io.SAXReader;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

public class TestCaseWriter extends BaseWriter {

    private File outputDir;

    public TestCaseWriter(Environment env) {
        super(env);
        this.outputDir = new File(env.getProperty("generator.testcases-path", ""));
    }

    @Override
    public void update(Observable o, Object arg) {
        OutputStub outputStub = (OutputStub) o;


        writeTestCases(outputStub);
    }

    private void writeTestCases(OutputStub outputStub)
    {
        outputStub.getTemplates().forEach((version, template) -> {
            template.getControllers().forEach(controllerStub -> {
                controllerStub.getActions().forEach((actionName, actionStub) -> {
                    try {
                        String url = "/" + version + "/" + controllerStub.getName() + "/" + actionName;
                        File file = new File(outputDir.getAbsolutePath() + url + ".yml");
                        file.getParentFile().mkdirs();
                        if (file.exists()) {
                            return;
                        }
                        file.createNewFile();

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);


                        outputStreamWriter.write("---\n");
                        outputStreamWriter.write("api: /api" + url + "\n");

                        actionStub.getRequests().forEach((requestName, requestStub) -> {
                            try {
                                outputStreamWriter.write(requestName + ":\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        outputStreamWriter.write("---\n");


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
