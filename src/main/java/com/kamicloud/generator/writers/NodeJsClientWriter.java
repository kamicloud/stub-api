package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.OutputStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Locale;

public class NodeJsClientWriter extends BaseWriter {
    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

    @Override
    void postConstruct() {

    }

    @Override
    void update(OutputStub o) {


        Locale locale = Locale.forLanguageTag("cn-zh");
        Context context = new Context(locale);
        context.setVariable("user", "xxx");
        context.setVariable("baseUrl", "xxx");
        String content = springTemplateEngine.process("home", context);


    }
}
