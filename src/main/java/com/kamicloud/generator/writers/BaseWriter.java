package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.GeneratorProperties;
import com.kamicloud.generator.stubs.core.OutputStub;
import com.kamicloud.generator.utils.StringUtil;
import com.kamicloud.generator.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.util.*;

@Component
abstract class BaseWriter implements Observer {
    File dir = new File("");
    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

    @Autowired
    protected Environment env;

    @Autowired
    protected UrlUtil urlUtil;

    @Autowired
    protected StringUtil stringUtil;

    @Autowired
    GeneratorProperties generatorProperties;

    protected HashMap<String, String> processes = new HashMap<>();

    @Override
    public void update(Observable o, Object arg) {
        String name = getName();
        ArrayList<String> writers;
        GeneratorProperties.Process process = generatorProperties.getProcess();

        if (env.getProperty("process", "").equals("laravel-auto-test")) {
            writers = process.getLaravelAutoTest();
        } else {
            writers = process.getDefaults();
        }

        if (writers == null || !writers.contains(name)) {
            return;
        }

        postConstruct();
        update((OutputStub) o);
    }

    abstract String getName();

    abstract void postConstruct();

    abstract void update(OutputStub o);
}
