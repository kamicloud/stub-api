package com.kamicloud.stub.javaclient;

import com.kamicloud.stub.core.generators.BaseGenerator;
import com.kamicloud.stub.core.stubs.OutputStub;
import com.kamicloud.stub.core.stubs.TemplateStub;

import java.io.File;
import java.util.Objects;

public class JavaClientGenerator extends BaseGenerator {
    private String version;

    private File outputDir;

    @Override
    public void postConstruct() {

    }

    @Override
    public void render(OutputStub output) {
        String javaClientPath = Objects.requireNonNull(env.getProperty("generator.java-client.path"));
        String javaClasspath = Objects.requireNonNull(env.getProperty("generator.java-client.classpath"));
        outputDir = new File(javaClientPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        output.getTemplates().forEach((version, templateStub) -> {
            this.version = version;

            try {
                writePojos(templateStub);
//                writeModels(templateStub);
//                writeHttp(templateStub);
//                writeEnums(templateStub);
//                writeRoute(templateStub);
//                writeErrors(templateStub);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void writePojos(TemplateStub templateStub) {
    }
}
