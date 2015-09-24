package com.mycity4kids.models.profile;

import com.mycity4kids.models.basemodel.BaseDataModel;


public class ViewProfileResponse extends BaseDataModel{
	private int responseCode;
	private String response;
	private ViewProfileResult result;

	/**
	 * @return the result
	 */
	public ViewProfileResult getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(ViewProfileResult result) {
		this.result = result;
	}

	private boolean isLoggedIn;

	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}


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

}
