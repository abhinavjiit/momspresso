package com.mycity4kids.models.response;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersFollowingResult {
    private String userId;
    private String followerId;
    private String followingId;
    private String firstName;
    private String lastName;
    private ProfilePic profilePicUrl;
    private int isFollowed;

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

    public int getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(int isFollowed) {
        this.isFollowed = isFollowed;
    }
}
