package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchTopicResult {
    private String id;
    private String slug;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
