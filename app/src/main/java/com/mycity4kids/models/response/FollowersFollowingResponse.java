package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersFollowingResponse extends BaseResponse {
    @SerializedName("data")
    private FollowersFollowingData data;

    public FollowersFollowingData getData() {
        return data;
    }

    public void setData(FollowersFollowingData data) {
        this.data = data;
    }
}
