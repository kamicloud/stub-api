package com.kamicloud.generator.config;

import com.kamicloud.generator.parsers.DocParser;
import com.kamicloud.generator.parsers.Parser;
import com.kamicloud.generator.stubs.core.OutputStub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfiguration {
    private OutputStub outputStub;
    @Bean
    public Parser parser() {
        return new Parser();
    }

    @Bean
    public DocParser docParser() {
        return new DocParser();
    }

    @Bean
    public OutputStub outputStub() {
        if (outputStub == null) {
            outputStub = new OutputStub();
        }
        return outputStub;
    }
}
