package com.github.kamicloud.stub.core.segments.openapi.v3;

import com.github.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.LinkedHashMap;

public class ComponentSegment implements YamlSerializable {
    private final LinkedHashMap<String, SchemaSegment> schemas = new LinkedHashMap<>();

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>() {{
            put("schemas", new LinkedHashMap<String, Object>(){{
                schemas.forEach((s, schema) -> {
                    put(s, schema.toYaml());
                });
            }});
        }};
    }

    public LinkedHashMap<String, SchemaSegment> getSchemas() {
        return schemas;
    }
}
