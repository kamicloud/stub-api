package com.kamicloud.stub.core.interfaces;

public interface PHPNamespacePathTransformerInterface {
    String namespaceToPath(String namespace);
    String pathToNamespace(String path);
}
