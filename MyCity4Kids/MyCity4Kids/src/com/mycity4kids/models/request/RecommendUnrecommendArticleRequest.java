package com.mycity4kids.models.request;

import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 28/7/16.
 */
public class RecommendUnrecommendArticleRequest {
    private String articleId;
    private String status;

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
}
