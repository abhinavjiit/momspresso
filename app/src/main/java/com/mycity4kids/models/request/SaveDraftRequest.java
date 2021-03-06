package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 26/9/18.
 */

public class SaveDraftRequest {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("articleType")
    private String articleType;
    @SerializedName("userAgent1")
    private String userAgent1;
    @SerializedName("tags")
    private List<Map<String, String>> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getUserAgent1() {
        return userAgent1;
    }

    public void setUserAgent1(String userAgent1) {
        this.userAgent1 = userAgent1;
    }

    public List<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, String>> tags) {
        this.tags = tags;
    }
}
