package com.mycity4kids.models.editor;

import java.io.Serializable;

/**
 * Created by anshul on 3/16/16.
 */
public class ArticleDraftList implements Serializable{
private String id;
    private String title;
    private String body;
    private String user_id;
    private boolean status;
    private String updated;

    public String getUpdatedDate() {
        return updated;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updated = updatedDate;
    }

    public String getTitle() {
        return title;
    }

    public boolean getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getUserId() {
        return user_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }
}
