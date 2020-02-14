package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/11/16.
 */
public class BlogPageResult {
    @SerializedName("isSetup")
    private int isSetup;
    @SerializedName("blogTitle")
    private String blogTitle;
    @SerializedName("userBio")
    private String userBio;

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public int getIsSetup() {
        return isSetup;
    }

    public void setIsSetup(int isSetup) {
        this.isSetup = isSetup;
    }
}
