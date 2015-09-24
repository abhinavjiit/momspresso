package com.mycity4kids.models.businesseventdetails;

import com.mycity4kids.models.BaseResponseModel;

public class DetailsResponse extends BaseResponseModel{
	private DetailsResult result;

	public DetailsResult getResult() {
		return result;
	}

	public void setResult(DetailsResult result) {
		this.result = result;
	}

	

}
