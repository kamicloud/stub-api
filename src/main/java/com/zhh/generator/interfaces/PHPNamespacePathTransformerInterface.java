package com.zhh.generator.interfaces;

public interface PHPNamespacePathTransformerInterface {
    public String namespaceToPath(String namespace);
    public String pathToNamespace(String path);
}
