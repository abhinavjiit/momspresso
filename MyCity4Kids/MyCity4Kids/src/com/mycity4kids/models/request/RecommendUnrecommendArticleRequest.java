package com.mycity4kids.models.request;

/**
 * Created by hemant on 28/7/16.
 */
public class RecommendUnrecommendArticleRequest {
    private String articleId;
    private String status;
    private String type;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
