package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchAuthorResult {
    @SerializedName("userId")
    private String userId;
    @SerializedName("profile_image")
    private ProfilePic profile_image;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("isFollowed")
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
