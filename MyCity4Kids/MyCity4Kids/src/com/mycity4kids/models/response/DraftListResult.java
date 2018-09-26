package com.mycity4kids.models.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 25/8/16.
 */
public class DraftListResult implements Serializable {
    String id;
    String articleType;
    Long updatedTime;
    String createdTime;
    String body;
    String title;
    //    ImageURL imageUrl;
    int itemType = 1;
    //    private Map<String, List<Map<String, String>>> tags;
    private ArrayList<Map<String, String>> tags;
    private String userAgent1;

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

//    public ImageURL getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(ImageURL imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

//    public Map<String, List<Map<String, String>>> getTags() {
//        return tags;
//    }
//
//    public void setTags(Map<String, List<Map<String, String>>> tags) {
//        this.tags = tags;
//    }

    public ArrayList<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Map<String, String>> tags) {
        this.tags = tags;
    }

    public String getUserAgent1() {
        return userAgent1;
    }

    public void setUserAgent1(String userAgent1) {
        this.userAgent1 = userAgent1;
    }
}
