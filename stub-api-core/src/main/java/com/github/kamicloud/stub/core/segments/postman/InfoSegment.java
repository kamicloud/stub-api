package com.github.kamicloud.stub.core.segments.postman;

public class InfoSegment {
    private String _postman_id;
    private String name = "API Generator";
    private String schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";

    public InfoSegment() {
        this._postman_id = "xxxxxxxxxxxx";
    }

    public String get_postman_id() {
        return _postman_id;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }
}
