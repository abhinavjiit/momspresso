package com.mycity4kids.models.response;

public class LikeReactionModel {

    private String reaction;
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
