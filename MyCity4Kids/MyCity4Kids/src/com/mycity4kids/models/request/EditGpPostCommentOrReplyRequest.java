package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 24/5/18.
 */

public class EditGpPostCommentOrReplyRequest {
    @SerializedName("content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
