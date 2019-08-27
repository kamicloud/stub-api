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
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Component
abstract class BaseWriter implements Observer {
    File dir = new File("");

    @Autowired
    protected Environment env;

    @Autowired
    protected UrlUtil urlUtil;

    @Autowired
    protected StringUtil stringUtil;

    protected HashMap<String, String> processes = new HashMap<>();

    @Override
    public void update(Observable o, Object arg) {
        String name = getName();
        String process = env.getProperty("process", "");

        if (process.equals("")) {
            process = "default";
        }

        process = env.getProperty("generator.process." + process, "");
        if (process.equals("")) {
            return;
        }

        String[] writers = process.split(",");

        Arrays.asList(writers).forEach(s -> {
            processes.put(s, s);
        });

        if (!processes.isEmpty() && !processes.containsKey(name)) {
            return;
        }

        postConstruct();
        update((OutputStub) o);
    }

    abstract String getName();

    abstract void postConstruct();

    abstract void update(OutputStub o);
}
