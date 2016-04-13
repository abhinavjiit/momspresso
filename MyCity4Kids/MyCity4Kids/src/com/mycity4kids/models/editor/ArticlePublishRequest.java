package com.mycity4kids.models.editor;

/**
 * Created by anshul on 3/19/16.
 */
public class ArticlePublishRequest {
    private String userId;
    private String title;
    private String body;
    private String id;
    private String draftId;
    private String sourceId;
    private String moderation_status;
    private String node_id;


        private String imageUrl;


    public String getBody() {
        return body;
    }

    public String getId() {
        return id;
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



    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser_id(String user_id) {
        this.userId = user_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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
}
