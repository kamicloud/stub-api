package com.kamicloud.generator.utils;

import com.google.common.base.CaseFormat;
import com.kamicloud.generator.config.DefaultProfileUtil;

public class UrlUtil {
//    public static escape
    public static String transformVersion(String version) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, version);
    }

    public static String transformController(String controller) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, controller);
    }

    public static String transformAction(String action) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, action);
    }

    public static String getUrlPrefix() {
        return getUrlPrefix(false);
    }

    public static String getUrlPrefix(boolean noRoot) {
        return (noRoot ? "" : "/") + DefaultProfileUtil.getEnv().getProperty("api-prefix", "api");
    }

    public static String getUrlWithPrefix(String version, String controller, String action) {
        return String.join("/", getUrlPrefix(), getUrl(version, controller, action));
    }

    private static String getUrl(String version, String controller, String action) {
        return String.join("/", transformVersion(version), transformController(controller), transformAction(action));
    }

}
