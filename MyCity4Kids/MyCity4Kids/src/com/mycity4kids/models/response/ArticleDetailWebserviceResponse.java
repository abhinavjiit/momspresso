package com.mycity4kids.models.response;

/**
 * Created by hemant on 27/3/17.
 */
public class ArticleDetailWebserviceResponse extends BaseResponse {
    private ArticleDetailResult data;

    public ArticleDetailResult getData() {
        return data;
    }

    public void setData(ArticleDetailResult data) {
        this.data = data;
    }
}
