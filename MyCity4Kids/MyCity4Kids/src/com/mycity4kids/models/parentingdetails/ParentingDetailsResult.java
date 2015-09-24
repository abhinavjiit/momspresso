package com.mycity4kids.models.parentingdetails;

import com.mycity4kids.models.CommonMessage;

public class ParentingDetailsResult extends CommonMessage{
	private ParentingDetailsData data;

	public ParentingDetailsData getData() {
		return data;
	}

	public void setData(ParentingDetailsData data) {
		this.data = data;
	}
}
