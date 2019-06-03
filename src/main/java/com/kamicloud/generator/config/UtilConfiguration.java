package com.kamicloud.generator.config;

import com.kamicloud.generator.utils.StringUtil;
import com.kamicloud.generator.utils.UrlUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfiguration {
    @Bean
    public UrlUtil urlUtil() {
        return new UrlUtil();
    }

    @Bean
    public StringUtil stringUtil() {
        return new StringUtil();
    }
}
