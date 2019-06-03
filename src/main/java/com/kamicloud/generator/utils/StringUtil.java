package com.kamicloud.generator.utils;

import com.google.common.base.CaseFormat;

public class StringUtil {
    public String lowerCamelToLowerUnderscore(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }
}
