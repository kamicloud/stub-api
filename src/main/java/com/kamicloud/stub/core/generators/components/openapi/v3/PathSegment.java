package com.kamicloud.stub.core.generators.components.openapi.v3;

import com.kamicloud.stub.core.interfaces.YamlSerializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PathSegment implements YamlSerializable {
    private PathEntity get;
    private PathEntity post;

    @Override
    public LinkedHashMap<String, Object> toYaml() {
        return new LinkedHashMap<String, Object>(){{
            if (get != null) {
                put("get", get.toYaml());
            }
            if (post != null) {
                put("post", post.toYaml());
            }
        }};
    }

    public PathEntity getPost() {
        return post;
    }

    public void setPost(PathEntity post) {
        this.post = post;
    }

    public PathEntity getGet() {
        return get;
    }

    public void setGet(PathEntity get) {
        this.get = get;
    }

    public static class PathEntity implements YamlSerializable {
        private final ArrayList<String> tags = new ArrayList<>();
        private String summary;
        private final LinkedHashMap<String, PathResponseSegment> responses = new LinkedHashMap<>();
        private PathRequestBodySegment requestBody;

        @Override
        public LinkedHashMap<String, Object> toYaml() {
            return new LinkedHashMap<String, Object>(){{
                put("tags", tags);
                if (summary != null) {
                    put("summary", summary);
                }
                put("responses", new LinkedHashMap<String, Object>() {{
                    responses.forEach((s, response) -> put(s, response.toYaml()));
                }});
                if (requestBody != null) {
                    put("requestBody", requestBody.toYaml());
                }
            }};
        }

        public ArrayList<String> getTags() {
            return tags;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public LinkedHashMap<String, PathResponseSegment> getResponses() {
            return responses;
        }

        public void setRequestBody(PathRequestBodySegment requestBody) {
            this.requestBody = requestBody;
        }

        public PathRequestBodySegment getRequestBody() {
            return requestBody;
        }
    }
}
