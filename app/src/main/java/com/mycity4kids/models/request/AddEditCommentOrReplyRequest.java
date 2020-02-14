package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 6/6/18.
 */

public class AddEditCommentOrReplyRequest {

    @SerializedName("post_id")
    private String post_id;
    @SerializedName("message")
    private String message;
    @SerializedName("parent_id")
    private String parent_id;
    @SerializedName("type")
    private String type;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
