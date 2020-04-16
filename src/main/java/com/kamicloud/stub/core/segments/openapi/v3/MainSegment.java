package com.kamicloud.stub.core.segments.openapi.v3;

import com.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MainSegment implements YamlSerializable {
    private String openapi = "3.0.1";
    private InfoSegment info;
    private ExternalDocSegment externalDocs;
    private final LinkedList<String> servers = new LinkedList<>();
    private final ArrayList<TagSegment> tags = new ArrayList<>();
    private final LinkedHashMap<String, PathSegment> paths = new LinkedHashMap<>();
    private ComponentSegment components;

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>(){{
            put("openapi", openapi);
            if (info != null) {
                put("info", info.toYaml());
            }
            put("paths", new LinkedHashMap<String, Object>() {{
                paths.forEach((s, path) -> put(s, path.toYaml()));
            }});
            if (components != null) {
                put("components", components.toYaml());
            }
        }};
    }

    public MainSegment setOpenapi(String openapi) {
        this.openapi = openapi;
        return this;
    }

    public MainSegment setInfo(InfoSegment info) {
        this.info = info;
        return this;
    }

    public ArrayList<TagSegment> getTags() {
        return tags;
    }

    public LinkedHashMap<String, PathSegment> getPaths() {
        return paths;
    }

    public ComponentSegment getComponents() {
        return components;
    }

    public void setComponents(ComponentSegment components) {
        this.components = components;
    }
}
