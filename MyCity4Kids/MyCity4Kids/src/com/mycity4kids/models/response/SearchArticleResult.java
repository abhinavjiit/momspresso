package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchArticleResult {

    private String id;
    private String userId;
    private String titleSlug;
    private String image;
    private String title;
    private String body;
    private String blogSlug;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBlogSlug() {
        return blogSlug;
    }

    public void setBlogSlug(String blogSlug) {
        this.blogSlug = blogSlug;
    }
}
