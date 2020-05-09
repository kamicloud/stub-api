package com.github.kamicloud.stub.core.segments.postman;

public class ParameterSegment {
    private String key;
    private String value = "";
    private String type = "text";
    private String description = "";
    private Boolean disabled = false;

    public ParameterSegment(String key) {
        this.key = key;
    }

    public ParameterSegment(String key, String value) {
        this(key);
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
