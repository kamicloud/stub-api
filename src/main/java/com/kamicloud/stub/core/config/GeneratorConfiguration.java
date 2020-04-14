package com.kamicloud.stub.core.config;

import com.kamicloud.stub.core.generators.*;
import com.kamicloud.stub.laravel.LaravelDocGenerator;
import com.kamicloud.stub.laravel.LaravelAutoTestGenerator;
import com.kamicloud.stub.laravel.LaravelGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class GeneratorConfiguration {

    @Bean
    public PostmanGenerator postmanGenerator() {
        return new PostmanGenerator();
    }

    @Bean
    public LaravelGenerator laravelWriter() {
        return new LaravelGenerator();
    }

    @Bean
    public JavaClientGenerator javaClientWriter() {
        return new JavaClientGenerator();
    }

    @Bean
    public TestCaseGenerator testCaseWriter() {
        return new TestCaseGenerator();
    }

    @Bean
    public LaravelDocGenerator docWriter() {
        return new LaravelDocGenerator();
    }

    @Bean
    public LaravelAutoTestGenerator autoTestWriter() {
        return new LaravelAutoTestGenerator();
    }

    @Bean
    public NodeJsClientGenerator nodeJsClientWriter() {
        return new NodeJsClientGenerator();
    }

    @Bean
    public OpenAPIGenerator openAPIGenerator() {
        return new OpenAPIGenerator();
    }
}
