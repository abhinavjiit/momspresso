package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 4/1/17.
 */
public class VlogsListingResponse extends BaseResponse {
    @SerializedName("data")
    private List<VlogsListingData> data;

    public List<VlogsListingData> getData() {
        return data;
    }

    public void setData(List<VlogsListingData> data) {
        this.data = data;
    }
}
