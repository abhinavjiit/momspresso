package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchAuthorResult {
    private String userId;
    private ProfilePic profile_image;
    private String first_name;
    private String last_name;
    private int isFollowed;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProfilePic getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(ProfilePic profile_image) {
        this.profile_image = profile_image;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(int isFollowed) {
        this.isFollowed = isFollowed;
    }
}
