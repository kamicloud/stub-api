package com.kamicloud.stub.core;

import com.kamicloud.stub.core.config.DefaultProfileUtil;
import com.kamicloud.stub.core.config.StubApiCoreProperties;
import com.kamicloud.stub.core.parsers.DocParser;
import com.kamicloud.stub.core.parsers.Parser;
import com.kamicloud.stub.core.stubs.OutputStub;
import com.kamicloud.stub.core.generators.BaseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class GeneratorDriver {
    private final ArrayList<BaseGenerator> generators = new ArrayList<>();

    private final StubApiCoreProperties config;
    private final Parser parser;
    private final DocParser docParser;
    private final OutputStub output;
    private final ApplicationContext applicationContext;

    @Autowired
    public GeneratorDriver(
        StubApiCoreProperties config,
        Parser parser,
        DocParser docParser,
        OutputStub output,
        ApplicationContext applicationContext
    ) {
        this.config = config;

        this.parser = parser;
        this.docParser = docParser;
        this.output = output;
        this.applicationContext = applicationContext;

        DefaultProfileUtil.setConfig(config);
    }

    public void run() {
        // 解析模板和注释
        parser.parse();
        docParser.parse();

        // 分析结束同步数据
        output.postParse();

        loadServices();

        this.generators.forEach(generator -> {
            generator.render(output);
        });
    }

    /**
     * Load all writers
     */
    void loadServices() {
        ArrayList<String> generators = config.getProcess().getGenerators();
        generators.forEach(generator -> {
            try {
                BaseGenerator generatorInstance = (BaseGenerator) applicationContext.getBean(Class.forName(generator));
                this.generators.add(generatorInstance);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
