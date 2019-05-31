package com.kamicloud.generator.config;

import com.kamicloud.generator.writers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy
public class WriterConfiguration {

    @Bean
    public PostmanWriter postmanWriter() {
        return new PostmanWriter();
    }

    @Bean
    public LaravelWriter laravelWriter() {
        return new LaravelWriter();
    }

    @Bean
    public JavaClientWriter javaClientWriter() {
        return new JavaClientWriter();
    }

    @Bean
    public TestCaseWriter testCaseWriter() {
        return new TestCaseWriter();
    }

    @Bean
    public DocWriter docWriter() {
        return new DocWriter();
    }

    @Bean
    public AutoTestWriter autoTestWriter() {
        return new AutoTestWriter();
    }
}
