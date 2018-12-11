package com.kamicloud.generator.interfaces;

public interface StringEnumValueInterface {
    String value = "";

    default String value() {
        return value;
    }
}
