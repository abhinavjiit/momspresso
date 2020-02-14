package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 6/7/16.
 */
public class ArticleDetailResponse extends BaseResponse {

    @SerializedName("data")
    private ArticleDetailData data;

    public ArticleDetailData getData() {
        return data;
    }

    public void setData(ArticleDetailData data) {
        this.data = data;
    }
}
