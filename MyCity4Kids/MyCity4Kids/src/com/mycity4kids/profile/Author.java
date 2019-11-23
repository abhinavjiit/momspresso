package com.mycity4kids.profile;

import com.mycity4kids.models.response.ProfilePic;

public class Author {
    private String id;
    private String firstName;
    private String lastName;
    private String blogTitleSlug;
    private String userType;
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