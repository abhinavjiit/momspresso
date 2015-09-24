package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

import com.mycity4kids.models.BaseResponseModel;

public class CommonParentingResponse extends BaseResponseModel implements Serializable{
	
	private static final long serialVersionUID = -1055662135910795156L;
	private CommongParentingResult result;

	public CommongParentingResult getResult() {
		return result;
	}

	public void setResult(CommongParentingResult result) {
		this.result = result;
	}

}
