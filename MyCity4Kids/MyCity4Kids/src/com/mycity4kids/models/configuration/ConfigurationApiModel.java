package com.mycity4kids.models.configuration;

import com.kelltontech.model.BaseModel;


public class ConfigurationApiModel extends BaseModel{
	private int responseCode;
	private String response;
	private ConfigurationResult result;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public ConfigurationResult getResult() {
		return result;
	}
	public void setResult(ConfigurationResult result) {
		this.result = result;
	}

}
