package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 7/5/16.
 */
public class PublishDraftObject implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("articleType")
    private String articleType;
    @SerializedName("updatedTime")
    private Long updatedTime;
    @SerializedName("createdTime")
    private String createdTime;
    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;
    @SerializedName("imageUrl")
    private ImageURL imageUrl;
    @SerializedName("tags")
    private List<Map<String, String>> tags;
    @SerializedName("itemType")
    private int itemType = 1;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public List<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, String>> tags) {
        this.tags = tags;
    }

    public ImageURL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageURL imageUrl) {
        this.imageUrl = imageUrl;
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

}
