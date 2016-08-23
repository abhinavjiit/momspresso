package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListResult  {
    String firstName;
    String lastName;
    String userType;
    String UserBio;
    String id;
    Long followersCount;
    int isFollowed;
    String colorCode;

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    Long rank;

    public ProfilePic getProfilePic() {
        return profilePicUrl;
    }

    public void setProfilePic(ProfilePic profilePic) {
        this.profilePicUrl = profilePic;
    }

    ProfilePic profilePicUrl;
    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public int getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(int isFollowed) {
        this.isFollowed = isFollowed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbout() {
        return UserBio;
    }

    public void setAbout(String about) {
        this.UserBio = about;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


}
