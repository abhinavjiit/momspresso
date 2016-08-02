package com.mycity4kids.models.request;

/**
 * Created by hemant on 27/7/16.
 */
public class AddCommentRequest {
    private String articleId;
    private String userComment;

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
}
