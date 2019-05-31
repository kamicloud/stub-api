package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

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

    @Override
    public void update(Observable o, Object arg) {
        update((OutputStub) o);
    }

    abstract void update(OutputStub o);
}
