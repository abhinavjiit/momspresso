package com.mycity4kids.models.editor;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 3/14/16.
 */
public class ArticleDraftRequest {
    private String userId;
    private String title;
    private String body;
    private String id;
    private String status;
    private String imageName;
    private String sourceId;
    private String imageUrl;
    private String articleType;
    private List<Map<String, String>> tags;

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

    public class Tags {

    }
}
