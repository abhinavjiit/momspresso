package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/2/16.
 */
public class UserCommentsResponse extends BaseResponse {
    public UserCommentData getData() {
        return data;
    }

    public void setData(UserCommentData data) {
        this.data = data;
    }

    UserCommentData data;
}
