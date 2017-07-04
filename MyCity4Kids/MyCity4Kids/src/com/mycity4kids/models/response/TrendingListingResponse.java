package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 26/5/17.
 */
public class TrendingListingResponse extends BaseResponse {

    private List<TrendingListingData> data;

    public List<TrendingListingData> getData() {
        return data;
    }

    public void setData(List<TrendingListingData> data) {
        this.data = data;
    }
}