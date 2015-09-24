package com.mycity4kids.models.user;

import com.mycity4kids.models.CommonMessage;

public class UserResult extends CommonMessage{
	private UserModel data;

	/**
	 * @return the data
	 */
	public UserModel getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(UserModel data) {
		this.data = data;
	}

}
