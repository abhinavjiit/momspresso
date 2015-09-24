package com.mycity4kids.models.businesslist;

import com.mycity4kids.models.BaseResponseModel;

public class BusinessListResponse extends BaseResponseModel {
	
	private BusinessResult result;

	/**
	 * @return the result
	 */
	public BusinessResult getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(BusinessResult result) {
		this.result = result;
	}
	
}
