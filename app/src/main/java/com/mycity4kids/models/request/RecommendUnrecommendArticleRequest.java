package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 28/7/16.
 */
public class RecommendUnrecommendArticleRequest {
    @SerializedName("articleId")
    private String articleId;
    @SerializedName("status")
    private String status;
    @SerializedName("type")
    private String type;
    @SerializedName("is_live")
    private String is_live;

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

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }
}
