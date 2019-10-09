package com.kamicloud.generator;

import com.kamicloud.generator.config.ApplicationProperties;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.parsers.DocParser;
import com.kamicloud.generator.parsers.Parser;
import com.kamicloud.generator.stubs.core.OutputStub;
import com.kamicloud.generator.writers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class Generator {


    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    private final Environment env;
    private final PostmanWriter postmanWriter;
    private final LaravelWriter laravelWriter;
    private final TestCaseWriter testCaseWriter;
    private final DocWriter docWriter;
    private final AutoTestWriter autoTestWriter;
    private final NodeJsClientWriter nodeJsClientWriter;
    private final OpenAPIWriter openAPIWriter;
    private final Parser parser;
    private final DocParser docParser;
    private final OutputStub output;

    @Autowired
    public Generator(
        TestCaseWriter testCaseWriter,
        Environment env,
        PostmanWriter postmanWriter,
        LaravelWriter laravelWriter,
        DocWriter docWriter,
        AutoTestWriter autoTestWriter,
        NodeJsClientWriter nodeJsClientWriter,
        OpenAPIWriter openAPIWriter,
        Parser parser,
        DocParser docParser,
        OutputStub output
    ) {
        this.testCaseWriter = testCaseWriter;
        this.env = env;
        this.postmanWriter = postmanWriter;
        this.laravelWriter = laravelWriter;
        this.docWriter = docWriter;
        this.autoTestWriter = autoTestWriter;
        this.nodeJsClientWriter = nodeJsClientWriter;
        this.openAPIWriter = openAPIWriter;
        this.parser = parser;
        this.docParser = docParser;
        this.output = output;
    }

    @PostConstruct
    public void initApplication() {
        log.debug("logger start");
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
        output.addObserver(openAPIWriter);

        output.notifyObservers();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Generator.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.run(args);
    }
}
