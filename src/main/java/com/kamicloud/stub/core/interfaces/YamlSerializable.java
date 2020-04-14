package com.kamicloud.stub.core.interfaces;

import java.util.LinkedHashMap;

public interface YamlSerializable {
    LinkedHashMap<String, Object> toYaml();
}
