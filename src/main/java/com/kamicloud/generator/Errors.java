package com.kamicloud.generator;

import com.kamicloud.generator.annotations.ErrorInterface;

@SuppressWarnings("unused")
public enum Errors implements ErrorInterface {
    /**
     * comment 1
     */
    SERVER_INTERNAL_ERROR(10000),
    INVALID_PARAMETER(10001),

    // comment 2
    OBJECT_NOT_FOUND(10002),
    ;
//
    int value;

    Errors(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
