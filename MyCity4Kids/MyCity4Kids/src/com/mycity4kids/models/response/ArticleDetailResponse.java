package com.mycity4kids.models.response;

/**
 * Created by hemant on 6/7/16.
 */
public class ArticleDetailResponse extends BaseResponse {
    private ArticleDetailData data;

    public ArticleDetailData getData() {
        return data;
    }

    public void setData(ArticleDetailData data) {
        this.data = data;
    }
}
