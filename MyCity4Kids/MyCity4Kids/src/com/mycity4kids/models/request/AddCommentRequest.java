package com.mycity4kids.models.request;

/**
 * Created by hemant on 27/7/16.
 */
public class AddCommentRequest {
    private String articleId;
    private String userComment;
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
