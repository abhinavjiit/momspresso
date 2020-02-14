package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 26/5/17.
 */
public class TrendingListingResponse extends BaseResponse {
    @SerializedName("data")
    private List<TrendingListingData> data;

    public List<TrendingListingData> getData() {
        return data;
    }

    public void setData(List<TrendingListingData> data) {
        this.data = data;
    }
}