package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListResult {

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("userType")
    private String userType;
    @SerializedName("userBio")
    private String userBio;
    @SerializedName("id")
    private String id;
    @SerializedName("followersCount")
    private Long followersCount;
    @SerializedName("isFollowed")
    private int isFollowed;
    @SerializedName("colorCode")
    private String colorCode;
    @SerializedName("rank")
    private String rank;
    @SerializedName("ranks")
    private ArrayList<LanguageRanksModel> ranks;
    @SerializedName("profilePicUrl")
    private ProfilePic profilePicUrl;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ProfilePic getProfilePic() {
        return profilePicUrl;
    }

    public void setProfilePic(ProfilePic profilePic) {
        this.profilePicUrl = profilePic;
    }

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
        return userBio;
    }

    public void setAbout(String about) {
        this.userBio = about;
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

    public ArrayList<LanguageRanksModel> getRanks() {
        return ranks;
    }

    public void setRanks(ArrayList<LanguageRanksModel> ranks) {
        this.ranks = ranks;
    }
}
