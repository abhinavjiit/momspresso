package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingResponse extends BaseResponse {

    private ArticleListingData data;

    public ArticleListingData getData() {
        return data;
    }

    public void setData(ArticleListingData data) {
        this.data = data;
    }
}
