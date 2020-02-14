package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 5/7/16.
 */
public class ArticleListingResponse extends BaseResponse {

    @SerializedName("data")
    private List<ArticleListingData> data;

    public List<ArticleListingData> getData() {
        return data;
    }

    public void setData(List<ArticleListingData> data) {
        this.data = data;
    }
}
