package com.kamicloud.generator.config;

import com.kamicloud.generator.parsers.DocParser;
import com.kamicloud.generator.parsers.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfiguration {
    @Bean
    public Parser parser() {
        return new Parser();
    }

    @Bean
    public DocParser docParser() {
        return new DocParser();
    }
}
