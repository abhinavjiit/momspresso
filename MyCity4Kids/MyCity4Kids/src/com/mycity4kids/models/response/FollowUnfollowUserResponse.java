package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 2/8/16.
 */
public class FollowUnfollowUserResponse extends BaseResponse{
    @SerializedName("data")
    private FollowUnfollowUserData data;

    public FollowUnfollowUserData getData() {
        return data;
    }

    public void setData(FollowUnfollowUserData data) {
        this.data = data;
    }
}
