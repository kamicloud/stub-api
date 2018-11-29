package com.zhh.generator.interfaces;

public interface StringEnumValueInterface {
    String value = "";

    default String value() {
        return value;
    }
}
