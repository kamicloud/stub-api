package com.github.kamicloud.stub.core.config;

import com.github.kamicloud.stub.core.generators.OpenAPIGenerator;
import com.github.kamicloud.stub.core.generators.PostmanGenerator;
import com.github.kamicloud.stub.core.generators.TestCaseGenerator;
import com.github.kamicloud.stub.javaclient.JavaClientGenerator;
import com.github.kamicloud.stub.jsclient.NodeJsClientGenerator;
import com.github.kamicloud.stub.laravel.LaravelDocGenerator;
import com.github.kamicloud.stub.laravel.LaravelAutoTestGenerator;
import com.github.kamicloud.stub.laravel.LaravelGenerator;
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
