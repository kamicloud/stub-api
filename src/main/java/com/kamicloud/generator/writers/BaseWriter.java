package com.kamicloud.generator.writers;

import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.utils.FileUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

@Component
abstract class BaseWriter implements Observer {
    File dir = new File("");

    protected Environment env;

    BaseWriter() {
        this.env = DefaultProfileUtil.getEnv();
    }

    @Override
    public void update(Observable o, Object arg) {
        update((OutputStub) o);
    }

    abstract void update(OutputStub o);
}
