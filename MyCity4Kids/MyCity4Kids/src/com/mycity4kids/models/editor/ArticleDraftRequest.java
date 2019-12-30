package com.mycity4kids.models.editor;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 3/14/16.
 */
public class ArticleDraftRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("id")
    private String id;
    @SerializedName("status")
    private String status;
    @SerializedName("imageName")
    private String imageName;
    @SerializedName("sourceId")
    private String sourceId;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("articleType")
    private String articleType;
    @SerializedName("tags")
    private List<Map<String, String>> tags;
    @SerializedName("cities")
    private List<Map<String, String>> cities;
    @SerializedName("userAgent1")
    private String userAgent1;

    public String getBody() {
        return body;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_id() {
        return userId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser_id(String user_id) {
        this.userId = user_id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public List<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, String>> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public List<Map<String, String>> getCities() {
        return cities;
    }

    public void setCities(List<Map<String, String>> cities) {
        this.cities = cities;
    }

    public String getUserAgent1() {
        return userAgent1;
    }

    public void setUserAgent1(String userAgent1) {
        this.userAgent1 = userAgent1;
    }
}
