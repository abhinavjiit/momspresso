package com.mycity4kids.models.login;

public class Result {
	 private String message;
	  private boolean isActive;
	  private String userId;
	  
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isAlive() {
		return isActive;
	}
	public void setAlive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
