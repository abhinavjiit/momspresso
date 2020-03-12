package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersFollowingResult {

    @SerializedName(value = "userId", alternate = {"id"})
    private String userId;
    @SerializedName("followerId")
    private String followerId;
    @SerializedName("followingId")
    private String followingId;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("profilePicUrl")
    private ProfilePic profilePicUrl;
    @SerializedName("isFollowed")
    private boolean isFollowed;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ProfilePic getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(ProfilePic profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public boolean getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
}
