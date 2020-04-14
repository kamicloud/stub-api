package com.kamicloud.stub.core.generators;

import com.kamicloud.stub.core.stubs.core.OutputStub;
import com.kamicloud.stub.core.stubs.core.TemplateStub;
import com.kamicloud.stub.core.generators.components.java.ClassAttributeCombiner;
import com.kamicloud.stub.core.generators.components.java.ClassCombiner;
import com.kamicloud.stub.core.generators.components.java.ClassMethodCombiner;

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
        ClassCombiner.setRoot(javaClientPath, javaClasspath);
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
        templateStub.getModels().forEach((modelStub) -> {
            try {
                String modelName = modelStub.getName();
                ClassCombiner classCombiner = new ClassCombiner("models." + modelName + "POJO");

                modelStub.getParameters().forEach((parameterStub) -> {
                    String parameterName = parameterStub.getName();
                    new ClassAttributeCombiner(classCombiner, parameterName, "public");
                    new ClassMethodCombiner(classCombiner, "get" + parameterName);
                    new ClassMethodCombiner(classCombiner, "set" + parameterName);
                });

                classCombiner.toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
