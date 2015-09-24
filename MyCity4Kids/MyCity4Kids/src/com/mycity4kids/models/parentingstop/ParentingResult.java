package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

public class ParentingResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 904118943390444101L;
	private ParentingData data;
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ParentingData getData() {
		return data;
	}

	public void setData(ParentingData data) {
		this.data = data;
	}

}
