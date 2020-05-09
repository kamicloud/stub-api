package com.github.kamicloud.stub.core.utils;

import com.github.kamicloud.stub.core.config.DefaultProfileUtil;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Locale;

public class StringUtil {

    @Autowired
    protected SpringTemplateEngine springTemplateEngine;

    public String lowerCamelToLowerUnderscore(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    public static String transformLfToBr(String lf) {
        if (lf == null) {
            return null;
        }
        return lf.replace("\n", "<br>");
    }

    public String renderTemplate(String path, Object params) {
        Locale locale = Locale.forLanguageTag("cn-zh");
        Context context = new Context(locale);
        context.setVariable("params", params);
        context.setVariable("config", DefaultProfileUtil.getConfig());
        String content = springTemplateEngine.process(path, context);

        return content;
    }
}
