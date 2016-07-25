package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class AddCommentResponse extends BaseResponse{
    private AddCommentData data;

    public AddCommentData getData() {
        return data;
    }

    public void setData(AddCommentData data) {
        this.data = data;
    }
}
