package com.github.kamicloud.stub.core.segments.openapi.v3;

import com.github.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.LinkedHashMap;

public class PathRequestBodySegment implements YamlSerializable {
    private String description;
    private final LinkedHashMap<String, PathRequestBodyContentSegment> content = new LinkedHashMap<>();
    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>() {{
            put("content", new LinkedHashMap<String, Object>() {{
                content.forEach((s, content) -> put(s, content.toYaml()));
            }});
        }};
    }

    public LinkedHashMap<String, PathRequestBodyContentSegment> getContent() {
        return content;
    }
}
