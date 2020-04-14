package com.kamicloud.stub.core.generators.components.openapi.v3;

import com.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.LinkedHashMap;

public class InfoSegment implements YamlSerializable {
    private String title;
    private String description;
    private String version;
    private String host;
    private String basePath;

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>() {{
            put("title", title != null ? title : "API");
            if (description != null) {
                put("description", description);
            }
            if (version != null) {
                put("version", version);
            }
            if (host != null) {
                put("host", host);
            }
            if (basePath != null) {
                put("basePath", basePath);
            }
        }};
    }

    public InfoSegment setDescription(String description) {
        this.description = description;
        return this;
    }

    public InfoSegment setVersion(String version) {
        this.version = version;
        return this;
    }

    public InfoSegment setTitle(String title) {
        this.title = title;
        return this;
    }

    public InfoSegment setHost(String host) {
        this.host = host;
        return this;
    }

    public InfoSegment setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }
}
