package com.kamicloud.stub.core.generators;

import com.kamicloud.stub.core.config.StubApiCoreProperties;
import com.kamicloud.stub.core.stubs.OutputStub;
import com.kamicloud.stub.core.utils.StringUtil;
import com.kamicloud.stub.core.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
abstract public class BaseGenerator {
    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

    @Autowired
    protected Environment env;

    @Autowired
    protected StubApiCoreProperties config;

    @Autowired
    protected UrlUtil urlUtil;

    @Autowired
    protected StringUtil stringUtil;

    protected HashMap<String, String> processes = new HashMap<>();

    @PostConstruct
    public void postConstruct() {

    }

    public abstract void render(OutputStub o);
}
