package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/11/16.
 */
public class BlogPageResult {
    int isSetup;

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    String blogTitle;

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    String userBio;


    public int getIsSetup() {
        return isSetup;
    }

    public void setIsSetup(int isSetup) {
        this.isSetup = isSetup;
    }
}
