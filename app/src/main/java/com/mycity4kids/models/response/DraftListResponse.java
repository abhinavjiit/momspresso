package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/5/16.
 */
public class DraftListResponse extends BaseResponse {
    @SerializedName("data")
    private DraftListData data;

    public void setData(DraftListData data) {
        this.data = data;
    }

    public DraftListData getData() {
        return data;
    }
}
