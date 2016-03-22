package com.mycity4kids.models.editor;

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
}
