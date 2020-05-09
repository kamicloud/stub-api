package com.github.kamicloud.stub.core.stubs.components;

import com.google.common.base.CaseFormat;

public class StringVal {
    private final String raw;
    private final String LOWER_CAMEL;
    private final String UPPER_CAMEL;
    private final String UPPER_UNDERSCORE;
    private final String LOWER_UNDERSCORE;
    private final String LOWER_HYPHEN;


    public StringVal(String value, CaseFormat format) {
        this.raw = value;
        this.LOWER_CAMEL = format.to(CaseFormat.LOWER_CAMEL, value);
        this.UPPER_CAMEL = format.to(CaseFormat.UPPER_CAMEL, value);
        this.UPPER_UNDERSCORE = format.to(CaseFormat.UPPER_UNDERSCORE, value);
        this.LOWER_UNDERSCORE = format.to(CaseFormat.LOWER_UNDERSCORE, value);
        this.LOWER_HYPHEN = format.to(CaseFormat.LOWER_HYPHEN, value);
    }

    public String toString() {
        return raw;
    }

    public String toLowerCamelCase() {
        return LOWER_CAMEL;
    }

    public String getLOWER_CAMEL() {
        return LOWER_CAMEL;
    }

    public String toUpperCamelCaseCase() {
        return UPPER_CAMEL;
    }

    public String getUPPER_CAMEL() {
        return UPPER_CAMEL;
    }

    public String toUpperUnderscoreCase() {
        return UPPER_UNDERSCORE;
    }

    public String getUPPER_UNDERSCORE() {
        return UPPER_UNDERSCORE;
    }

    public String toLowerUnderscoreCase() {
        return LOWER_UNDERSCORE;
    }

    public String getLOWER_UNDERSCORE() {
        return LOWER_UNDERSCORE;
    }

    public String toLowerHyphenCase() {
        return LOWER_HYPHEN;
    }

    public String getLOWER_HYPHEN() {
        return LOWER_HYPHEN;
    }
}
