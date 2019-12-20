package com.mycity4kids.models.logout;

import com.google.gson.annotations.SerializedName;

public class LogoutResponse {
	@SerializedName("responseCode")
	private int responseCode;
	@SerializedName("response")
	private String response;
	@SerializedName("result")
	private Result result;


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
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}


}
