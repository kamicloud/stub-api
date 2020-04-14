package com.kamicloud.stub.core.utils;

import com.google.common.base.CaseFormat;
import com.kamicloud.stub.core.config.DefaultProfileUtil;

import java.util.HashMap;

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

    public static String transformModel(String model) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, model);
    }

    public static String getUrlPrefix() {
        return getUrlPrefix(false);
    }

    public static String getUrlPrefix(boolean noRoot) {
        return (noRoot ? "" : "/") + DefaultProfileUtil.getConfig().getApiPrefix();
    }

    public static String getUrlWithPrefix(String version, String controller, String action) {
        return String.join("/", getUrlPrefix(), getUrl(version, controller, action));
    }

    private static String getUrl(String version, String controller, String action) {
        return String.join("/", transformVersion(version), transformController(controller), transformAction(action));
    }

    public static HashMap<String, String> getResourceUrlWithPrefix(String version, String model, String id) {
        return new HashMap<String, String>() {{
            String base = String.join(
                "/",
                getUrlPrefix(),
                transformVersion(version),
                "restful",
                transformModel(model)
            );

            put("index", base);
            put("update", base + "/" + id);
            put("store", base);
            put("show", base + "/" + id);
            put("destroy", base + "/" + id);
        }};
    }

}
