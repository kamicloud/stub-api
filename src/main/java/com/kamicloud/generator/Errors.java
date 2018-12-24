package com.kamicloud.generator;

import com.kamicloud.generator.annotations.ErrorInterface;

public enum Errors implements ErrorInterface {
    /**
     * comment 1
     */
    SERVER_INTERNAL_ERROR(10000, ""),
    INVALID_PARAMETER(10001, ""),

    // comment 2
    OBJECT_NOT_FOUND(10002, ""),
    ;
//
    public final int value;
    public final String message;

    Errors(int value, String message) {
        this.value = value;
        this.message = message;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public String message() {
        return message;
    }
}
