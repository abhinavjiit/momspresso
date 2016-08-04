package com.mycity4kids.models.response;

/**
 * Created by hemant on 2/8/16.
 */
public class FollowUnfollowUserResponse extends BaseResponse{
    private FollowUnfollowUserData data;

    public FollowUnfollowUserData getData() {
        return data;
    }

    public void setData(FollowUnfollowUserData data) {
        this.data = data;
    }
}
