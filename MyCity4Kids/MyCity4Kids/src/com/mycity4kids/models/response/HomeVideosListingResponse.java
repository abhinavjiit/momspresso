package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 4/1/17.
 */
public class HomeVideosListingResponse extends BaseResponse {

    private List<HomeVideosListingData> data;

    public List<HomeVideosListingData> getData() {
        return data;
    }

    public void setData(List<HomeVideosListingData> data) {
        this.data = data;
    }
}
