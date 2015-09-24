package com.mycity4kids.models.parentingstop;

import com.mycity4kids.models.CommonMessage;

public class CommongParentingResult extends CommonMessage{

	private CommonParentingData data;

	public CommonParentingData getData() {
		return data;
	}

	public void setData(CommonParentingData data) {
		this.data = data;
	}
}
