package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 29/7/16.
 */
public class AddBookmarkResult {
    @SerializedName("id")
    private String id;
    @SerializedName("articleId")
    private String articleId;

    public String getBookmarkId() {
        return id;
    }

    public void setBookmarkId(String id) {
        this.id = id;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}
