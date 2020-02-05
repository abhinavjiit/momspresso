package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 27/3/17.
 */
public class ArticleDetailWebserviceResponse extends BaseResponse {
    @SerializedName("data")
    private ArticleDetailResult data;

    public ArticleDetailResult getData() {
        return data;
    }

    public void setData(ArticleDetailResult data) {
        this.data = data;
    }
}
