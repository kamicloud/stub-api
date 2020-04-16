package com.kamicloud.stub.core.segments.openapi.v3;

import com.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.LinkedHashMap;

public class PathResponseSegment implements YamlSerializable {
    private String description = "";
    private final LinkedHashMap<String, PathResponseContentEntity> content = new LinkedHashMap<>();
    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>() {{
            put("description", description);
            put("content", new LinkedHashMap<String, Object>() {{
                content.forEach((s, content) -> put(s, content.toYaml()));
            }});
        }};
    }

    public LinkedHashMap<String, PathResponseContentEntity> getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class PathResponseContentEntity implements YamlSerializable {
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
}
