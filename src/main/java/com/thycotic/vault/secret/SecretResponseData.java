package com.thycotic.vault.secret;

import java.util.Map;

public class SecretResponseData {
    private String id, path, version;
    private Map<String, Object> attributes;
    private Object data;

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getObjectData() {
        Map<String, Object> out = null;
        if (!(data instanceof String)) {
            out = (Map<String, Object>) data;
        }
        return out;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
