package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.core.OutputStub;
import com.kamicloud.generator.stubs.core.TemplateStub;
import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.common.MultiLinesCombiner;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class NodeJsClientWriter extends BaseWriter {

    protected File outputDir;

    @Override
    String getName() {
        return "nodejs-client";
    }

    @Override
    void postConstruct() {
        outputDir = new File(Objects.requireNonNull(env.getProperty("generator.writers.nodejs-client.output")));

        outputDir.mkdirs();
    }

    @Override
    void update(OutputStub o) {
        o.getTemplates().forEach(this::writeTemplate);

        writeTemplate("", o.getCurrentTemplate());
    }

    void writeTemplate(String version, TemplateStub templateStub) {
        FileCombiner file = new FileCombiner();

        file.setFileName(outputDir.getAbsolutePath() + "/API" + version + ".js");

        Locale locale = Locale.forLanguageTag("cn-zh");
        Context context = new Context(locale);
        context.setVariable("template", templateStub);
        String content = springTemplateEngine.process("js/api", context);

        file.addBlock(new MultiLinesCombiner(content));

        try {
            file.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
