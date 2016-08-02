package com.mycity4kids.models.response;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersFollowingResponse extends BaseResponse {
    private FollowersFollowingData data;

    public FollowersFollowingData getData() {
        return data;
    }

    public void setData(FollowersFollowingData data) {
        this.data = data;
    }
}
