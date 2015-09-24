package com.mycity4kids.models.recentlyviewed;

import com.mycity4kids.models.BaseResponseModel;

public class RecentlyResponse extends BaseResponseModel{
	private RecentlyResult result;

	public RecentlyResult getResult() {
		return result;
	}

	public void setResult(RecentlyResult result) {
		this.result = result;
	}
}
