package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchResponse extends BaseResponse {

    @SerializedName("data")
    private SearchData data;

    public SearchData getData() {
        return data;
    }

    public void setData(SearchData data) {
        this.data = data;
    }
}
