package com.mycity4kids.models.bookmark;

/**
 * Created by hemant on 29/10/15.
 */
public class BookmarkModel {
    private String id;
    private String category;
    private String action;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
