package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsDetailResponse extends BaseResponse {
    @SerializedName("data")
    private VlogsDetailData data;

    public VlogsDetailData getData() {
        return data;
    }

    public void setData(VlogsDetailData data) {
        this.data = data;
    }
}
