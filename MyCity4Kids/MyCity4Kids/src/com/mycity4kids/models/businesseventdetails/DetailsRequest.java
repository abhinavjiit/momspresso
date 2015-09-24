package com.mycity4kids.models.businesseventdetails;

public class DetailsRequest {
	private String categoryId;
	private String businessOrEventId;
	private String user_id;
	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getBusinessOrEventId() {
		return businessOrEventId;
	}
	public void setBusinessOrEventId(String businessOrEventId) {
		this.businessOrEventId = businessOrEventId;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
