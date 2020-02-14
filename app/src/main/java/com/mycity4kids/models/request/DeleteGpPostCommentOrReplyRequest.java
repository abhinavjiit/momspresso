package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 24/5/18.
 */

public class DeleteGpPostCommentOrReplyRequest {
    @SerializedName("isActive")
    private int isActive;

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
