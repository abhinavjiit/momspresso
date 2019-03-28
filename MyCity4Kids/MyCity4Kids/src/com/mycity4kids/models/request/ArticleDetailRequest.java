package com.mycity4kids.models.request;

/**
 * Created by hemant on 18/7/16.
 */
public class ArticleDetailRequest {
    private String articleId;
    private String contentType;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
