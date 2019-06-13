package com.kamicloud.generator;

import com.kamicloud.generator.config.ApplicationProperties;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.parsers.DocParser;
import com.kamicloud.generator.parsers.Parser;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.writers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@SuppressWarnings("unchecked")
public class Generator {
    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    @Autowired
    protected PostmanWriter postmanWriter;
    @Autowired
    protected LaravelWriter laravelWriter;
    @Autowired
    protected TestCaseWriter testCaseWriter;
    @Autowired
    protected DocWriter docWriter;
    @Autowired
    protected AutoTestWriter autoTestWriter;
    @Autowired
    protected NodeJsClientWriter nodeJsClientWriter;

    @Autowired
    public void setPostmanWriter(PostmanWriter postmanWriter) {
        this.postmanWriter = postmanWriter;
    }

    protected Parser parser;
    @Autowired
    protected DocParser docParser;

    @Autowired
    OutputStub output;

    @Autowired
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @PostConstruct
    public void initApplication() {
        DefaultProfileUtil.setEnv(env);

        // 解析模板和注释
        parser.parse();
        docParser.parse();

        // 分析结束同步数据
        output.postParse();

        // 注册处理器
        output.addObserver(postmanWriter);
        output.addObserver(testCaseWriter);
        output.addObserver(docWriter);
        output.addObserver(laravelWriter);
        output.addObserver(nodeJsClientWriter);
        output.addObserver(autoTestWriter);

        output.notifyObservers();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Generator.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.run(args);
    }
}
