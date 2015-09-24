package com.mycity4kids.models.version;

public class VersionResponse {
	private int responseCode;
	private String response;
	private VersionResult result;
	
	
	
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
	public VersionResult getResult() {
		return result;
	}
	public void setResult(VersionResult result) {
		this.result = result;
	}

}
