package com.kamicloud.stub.core;

import com.kamicloud.stub.core.config.ApplicationProperties;
import com.kamicloud.stub.core.config.DefaultProfileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class StubApi {
    private static final Logger log = LoggerFactory.getLogger(StubApi.class);

    GeneratorDriver generatorDriver;

    @Autowired
    public StubApi(GeneratorDriver generatorDriver) {
        this.generatorDriver = generatorDriver;
    }

    @PostConstruct
    public void initApplication() {
        log.debug("logger start");

        this.generatorDriver.run();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(com.kamicloud.stub.core.StubApi.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.run(args);
    }
}
