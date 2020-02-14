package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 8/2/16.
 */
public class UserCommentsResponse extends BaseResponse {
    @SerializedName("data")
    private UserCommentData data;

    public UserCommentData getData() {
        return data;
    }

    public void setData(UserCommentData data) {
        this.data = data;
    }
}
