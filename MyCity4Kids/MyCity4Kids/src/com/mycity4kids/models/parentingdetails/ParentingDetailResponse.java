package com.mycity4kids.models.parentingdetails;

import com.mycity4kids.models.BaseResponseModel;

public class ParentingDetailResponse extends BaseResponseModel{
	private ParentingDetailsResult result;

	public ParentingDetailsResult getResult() {
		return result;
	}

	public void setResult(ParentingDetailsResult result) {
		this.result = result;
	}
}
