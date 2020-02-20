package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 9/12/16.
 */
public class BloggerAnalyticsSocial {
    @SerializedName("likes")
    private String likes;
    @SerializedName("share")
    private String share;
    @SerializedName("comment")
    private String comment;

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
