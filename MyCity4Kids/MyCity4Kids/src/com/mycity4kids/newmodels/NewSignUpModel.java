package com.mycity4kids.newmodels;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by hemant on 19/1/16.
 */
public class NewSignUpModel extends BaseDataModel {
    private String username;
    private String color_code;
    private String mobileNumber;
    private String email;
    private String password;
    private String cityId;
    private String socialMode;
    private String socialToken;
    private String profileImgUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSocialMode() {
        return socialMode;
    }

    public void setSocialMode(String socialMode) {
        this.socialMode = socialMode;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public void setSocialToken(String socialToken) {
        this.socialToken = socialToken;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
