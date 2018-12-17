package com.kamicloud.generator.writers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Observer;

@Component
abstract class BaseWriter implements Observer {
    File dir = new File("");

    protected Environment env;

    BaseWriter(Environment env) {
        this.env = env;
    }
}
