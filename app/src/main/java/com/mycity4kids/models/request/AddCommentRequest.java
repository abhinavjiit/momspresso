package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 27/7/16.
 */
public class AddCommentRequest {
    @SerializedName("articleId")
    private String articleId;
    @SerializedName("userComment")
    private String userComment;
    @SerializedName("parentId")
    private String parentId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
