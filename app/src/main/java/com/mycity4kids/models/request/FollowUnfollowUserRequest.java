package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 2/8/16.
 */
public class FollowUnfollowUserRequest {
    @SerializedName("followerId")
    private String followerId;

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }
}
