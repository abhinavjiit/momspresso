package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchBlogResult {

    private String userId;
    private String titleSlug;
    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
