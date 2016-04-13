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
    private String path;
    private String moderation_status;
    private String node_id;
    private String source_id;

    public ArticleDraftList() {
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModeration_status() {
        return moderation_status;
    }

    public void setModeration_status(String moderation_status) {
        this.moderation_status = moderation_status;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }
}
