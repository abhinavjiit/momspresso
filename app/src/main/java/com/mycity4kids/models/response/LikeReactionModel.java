package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

public class LikeReactionModel {

    @SerializedName("reaction")
    private String reaction;
    @SerializedName("status")
    private String status;

    public String getReaction() {
        return reaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
