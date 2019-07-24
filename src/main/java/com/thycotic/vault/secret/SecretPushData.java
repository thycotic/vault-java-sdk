package com.thycotic.vault.secret;

import java.util.Map;

public class SecretPushData {

	private String id;
    private Map<String, Object> data;
    private String description;
    private Map<String, Object> attributes;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	
	public Map<String, Object> getData() {
		return data;
	}

}
