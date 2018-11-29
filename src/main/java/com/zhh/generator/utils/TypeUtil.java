package com.zhh.generator.utils;

public class TypeUtil {
    public static String getPHPType(String type) {
        if (type.startsWith("Enums.")) {

        }
        switch (type) {
            case "String":
                return "string";
            case "string":
                return "string";

            default:
                return "";
        }
    }
}
