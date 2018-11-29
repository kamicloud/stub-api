package com.zhh.generator.annotations;

public enum MethodType {
    GET, POST, PUT, PATCH, DELETE, UPDATE;

    public boolean match(String name) {
        return name().equalsIgnoreCase(name);
    }
}
