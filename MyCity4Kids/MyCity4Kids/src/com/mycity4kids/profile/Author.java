package com.mycity4kids.profile;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.ProfilePic;

public class Author {
    @SerializedName("id")
    private String id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("userType")
    private String userType;
    @SerializedName("profilePicUrl")
    private ProfilePic profilePicUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBlogTitleSlug() {
        return blogTitleSlug;
    }

    public void setBlogTitleSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ProfilePic getProfilePic() {
        return profilePicUrl;
    }

    public void setProfilePic(ProfilePic profilePic) {
        this.profilePicUrl = profilePic;
    }
}