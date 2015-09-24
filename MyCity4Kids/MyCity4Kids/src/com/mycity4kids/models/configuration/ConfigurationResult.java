package com.mycity4kids.models.configuration;


public class ConfigurationResult {

	private String message;
	private ConfigurationData data;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ConfigurationData getData() {
		return data;
	}
	public void setData(ConfigurationData data) {
		this.data = data;
	}

}
