package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 25/8/16.
 */
public class DraftListResult implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("articleType")
    private String articleType;
    @SerializedName("updatedTime")
    private Long updatedTime;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;
    @SerializedName("itemType")
    private int itemType = 1;
    @SerializedName("tags")
    private ArrayList<Map<String, String>> tags;
    @SerializedName("userAgent")
    private String userAgent;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public ArrayList<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Map<String, String>> tags) {
        this.tags = tags;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
