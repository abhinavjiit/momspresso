package com.mycity4kids.models.editor;

/**
 * Created by anshul on 3/19/16.
 */
public class ArticlePublishRequest {
    private String userId;
    private String title;
    private String body;
    private String id;

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
}
