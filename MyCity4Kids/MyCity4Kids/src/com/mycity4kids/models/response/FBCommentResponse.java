package com.mycity4kids.models.response;

/**
 * Created by hemant on 26/9/16.
 */
public class FBCommentResponse extends BaseResponse{
    private FBCommentData data;

    public FBCommentData getData() {
        return data;
    }

    public void setData(FBCommentData data) {
        this.data = data;
    }
}
