package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MomVlogListingResponse extends BaseResponse {

    @SerializedName("data")
    private VlogsListingData data;

    public VlogsListingData getData() {
        return data;
    }

    public void setData(VlogsListingData data) {
        this.data = data;
    }
}
