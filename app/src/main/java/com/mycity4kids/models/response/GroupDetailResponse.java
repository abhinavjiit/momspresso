package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/5/18.
 */

public class GroupDetailResponse extends BaseResponse {

    @SerializedName("data")
    private GroupDetailData data;

    public GroupDetailData getData() {
        return data;
    }

    public void setData(GroupDetailData data) {
        this.data = data;
    }
}
