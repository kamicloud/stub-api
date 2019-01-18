package com.kamicloud.generator.interfaces;

public interface PHPNamespacePathTransformerInterface {
    String namespaceToPath(String namespace);
    String pathToNamespace(String path);
}
