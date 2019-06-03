package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.common.MultiLinesCombiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class NodeJsClientWriter extends BaseWriter {
    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

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
        FileCombiner file = new FileCombiner();

        file.setFileName(outputDir.getAbsolutePath() + "/test.js");

        Locale locale = Locale.forLanguageTag("cn-zh");
        Context context = new Context(locale);
        context.setVariable("output", o);
        String content = springTemplateEngine.process("js/api", context);

        file.addBlock(new MultiLinesCombiner(content));

        try {
            file.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
