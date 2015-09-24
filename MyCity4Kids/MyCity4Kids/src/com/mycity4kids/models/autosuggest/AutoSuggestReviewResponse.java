package com.mycity4kids.models.autosuggest;

public class AutoSuggestReviewResponse {
	private int responseCode;
	private String response;
	private AutoSuggestReviewResult result;
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
	public AutoSuggestReviewResult getResult() {
		return result;
	}
	public void setResult(AutoSuggestReviewResult result) {
		this.result = result;
	}

}
