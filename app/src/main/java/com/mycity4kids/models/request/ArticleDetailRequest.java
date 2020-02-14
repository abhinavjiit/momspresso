package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 18/7/16.
 */
public class ArticleDetailRequest {
    @SerializedName("articleId")
    private String articleId;
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("authorId")
    private String authorId;
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("disableComment")
    private String disableComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisableComment() {
        return disableComment;
    }

    public void setDisableComment(String disableComment) {
        this.disableComment = disableComment;
    }

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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
