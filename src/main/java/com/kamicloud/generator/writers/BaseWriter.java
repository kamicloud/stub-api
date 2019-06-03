package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.utils.FileUtil;
import com.kamicloud.generator.utils.StringUtil;
import com.kamicloud.generator.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

@Component
abstract class BaseWriter implements Observer {
    File dir = new File("");

    @Autowired
    protected Environment env;

    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

    @Autowired
    protected UrlUtil urlUtil;

    @Autowired
    protected StringUtil stringUtil;

    @Override
    public void update(Observable o, Object arg) {
        String name = getName();

        if (!env.getProperty("generator.writers." + name + ".enabled", "false").equals("true")) {
            return;
        }

        postConstruct();
        update((OutputStub) o);
    }

    abstract String getName();

    abstract void postConstruct();

    abstract void update(OutputStub o);
}
