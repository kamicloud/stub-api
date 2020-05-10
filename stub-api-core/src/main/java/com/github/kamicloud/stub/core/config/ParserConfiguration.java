package com.github.kamicloud.stub.core.config;

import com.github.kamicloud.stub.core.parsers.DocParser;
import com.github.kamicloud.stub.core.parsers.Parser;
import com.github.kamicloud.stub.core.parsers.TestCaseParser;
import com.github.kamicloud.stub.core.stubs.OutputStub;
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
    public TestCaseParser testCaseParser() {
        return new TestCaseParser();
    }

    @Bean
    public OutputStub outputStub() {
        if (outputStub == null) {
            outputStub = new OutputStub();
        }
        return outputStub;
    }
}
