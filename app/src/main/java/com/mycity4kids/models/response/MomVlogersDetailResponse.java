package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

public class MomVlogersDetailResponse extends BaseResponse {
    @SerializedName("data")
    private MomVlogersDetailData data;

    public MomVlogersDetailData getData() {
        return data;
    }

    public void setData(MomVlogersDetailData data) {
        this.data = data;
    }
}
