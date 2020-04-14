package com.kamicloud.stub.core.generators.components.openapi.v3;

import com.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.LinkedHashMap;

public class PathRequestBodyContentSegment implements YamlSerializable {
    private SchemaSegment schema;

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>() {{
            if (schema != null) {
                put("schema", schema.toYaml());
            }
        }};
    }

    public SchemaSegment getSchema() {
        return schema;
    }

    public void setSchema(SchemaSegment schema) {
        this.schema = schema;
    }
}
