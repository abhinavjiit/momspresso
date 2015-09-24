package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

import com.mycity4kids.models.BaseResponseModel;

public class ParentingResponse  extends BaseResponseModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8618405529797901196L;
	/**
	 * 
	 */
	private ParentingResult result;

	public ParentingResult getResult() {
		return result;
	}

	public void setResult(ParentingResult result) {
		this.result = result;
	}

}
