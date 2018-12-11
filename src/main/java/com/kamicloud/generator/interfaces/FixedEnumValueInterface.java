package com.kamicloud.generator.interfaces;

public interface FixedEnumValueInterface {
    int value = 0;

    default int value() {
        return value;
    }
}
