package com.mycity4kids.models.parentingfilter;

import com.mycity4kids.models.CommonMessage;

public class ArticleFilterResult extends CommonMessage{
	private ArticleBlogFilterData data;

	public ArticleBlogFilterData getData() {
		return data;
	}

	public void setData(ArticleBlogFilterData data) {
		this.data = data;
	}

}
