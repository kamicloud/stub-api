package com.github.kamicloud.stub.core.segments.openapi.v3;

import com.github.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SchemaSegment implements YamlSerializable {
    private String type;
    private final ArrayList<String> required = new ArrayList<>();
    private final LinkedHashMap<String, Property> properties = new LinkedHashMap<>();
    private Property items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getRequired() {
        return required;
    }

    public LinkedHashMap<String, Property> getProperties() {
        return properties;
    }

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>(){{
            if (type != null) {
                put("type", type);
            }
            if (!required.isEmpty()) {
                put("required", required);
            }
            put("properties", new LinkedHashMap<String, Object>(){{
                properties.forEach((s, property) -> {
                    put(s, property.toYaml());
                });
            }});
            if (items != null) {
                put("items", items.toYaml());
            }
        }};
    }

    public void setItems(Property items) {
        this.items = items;
    }

    public static class Property implements YamlSerializable {
        private String type;
        private String format;
        private String ref;
        private boolean nullable;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        @Override
        public LinkedHashMap<String, Object> toYaml() {
            return new LinkedHashMap<String, Object>() {{
                if (type != null) {
                    put("type", type);
                }
                if (format != null) {
                    put("format", format);
                }
                if (ref != null) {
                    put("$ref", ref);
                }
                if (nullable) {
                    put("nullable", true);
                }
            }};
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public Boolean getNullable() {
            return nullable;
        }

        public void setNullable(Boolean nullable) {
            this.nullable = nullable;
        }
    }
}
