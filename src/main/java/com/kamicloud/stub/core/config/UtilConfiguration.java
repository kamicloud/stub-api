package com.kamicloud.stub.core.config;

import com.kamicloud.stub.core.utils.StringUtil;
import com.kamicloud.stub.core.utils.UrlUtil;
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
