package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.TemplateStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.writers.components.java.ClassAttributeCombiner;
import com.kamicloud.generator.writers.components.java.ClassCombiner;
import com.kamicloud.generator.writers.components.java.ClassMethodCombiner;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Objects;
import java.util.Observable;

public class JavaClientWriter extends BaseWriter {
    private String version;

    private File outputDir;
    private File generatedDir;
    private File routePath;

    @Override
    void postConstruct() {

    }

    @Override
    public void update(OutputStub output) {
        String javaClientPath = Objects.requireNonNull(env.getProperty("generator.java-client-path"));
        String javaClasspath = Objects.requireNonNull(env.getProperty("generator.java-client-classpath"));
        outputDir = new File(javaClientPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        ClassCombiner.setRoot(javaClientPath, javaClasspath);
        generatedDir = new File(outputDir.getAbsolutePath() + "/app/Generated");
        routePath = new File(outputDir.getAbsolutePath() + "/routes/generated_routes.php");
        FileUtil.deleteAllFilesOfDir(generatedDir);
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
        templateStub.getModels().forEach((modelName, modelStub) -> {
            try {
                ClassCombiner classCombiner = new ClassCombiner("models." + modelName + "POJO");

                modelStub.getParameters().forEach((parameterName, parameterStub) -> {
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
