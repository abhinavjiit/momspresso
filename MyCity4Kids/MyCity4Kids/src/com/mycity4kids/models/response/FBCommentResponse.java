package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 26/9/16.
 */
public class FBCommentResponse extends BaseResponse{
    @SerializedName("data")
    private FBCommentData data;

    public FBCommentData getData() {
        return data;
    }

    public void setData(FBCommentData data) {
        this.data = data;
    }
}
