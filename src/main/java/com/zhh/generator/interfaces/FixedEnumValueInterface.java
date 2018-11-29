package com.zhh.generator.interfaces;

public interface FixedEnumValueInterface {
    int value = 0;

    default int value() {
        return value;
    }
}
