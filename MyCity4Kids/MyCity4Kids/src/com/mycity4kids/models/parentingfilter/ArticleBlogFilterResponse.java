package com.mycity4kids.models.parentingfilter;

import com.mycity4kids.models.BaseResponseModel;

public class ArticleBlogFilterResponse extends BaseResponseModel{
	private ArticleFilterResult result;

	public ArticleFilterResult getResult() {
		return result;
	}

	public void setResult(ArticleFilterResult result) {
		this.result = result;
	}
}
