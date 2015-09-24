package com.mycity4kids.models.autosuggest;

public class AutoSuggestResponse {
	private int responseCode;
	private String response;
	private AutoSuggestResult result;
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
	public AutoSuggestResult getResult() {
		return result;
	}
	public void setResult(AutoSuggestResult result) {
		this.result = result;
	}

}
