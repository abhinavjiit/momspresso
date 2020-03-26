package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 2/8/16.
 */
public class FollowUnfollowUserRequest {

    @SerializedName("followerId")
    private String followerId;

    @SerializedName("followee_id")
    private String followee_id;

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowee_id() {
        return followee_id;
    }

    public void setFollowee_id(String followee_id) {
        this.followee_id = followee_id;
    }
}
