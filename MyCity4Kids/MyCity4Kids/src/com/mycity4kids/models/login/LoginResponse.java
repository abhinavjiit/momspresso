package com.mycity4kids.models.login;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * 
 * @author Deepanker Chaudhary
 *
 */
public class LoginResponse extends BaseDataModel{
	private int responseCode;
	private String responseMsg;
	private Result result;
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
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result pResult) {
		result = pResult;
	}

}
  