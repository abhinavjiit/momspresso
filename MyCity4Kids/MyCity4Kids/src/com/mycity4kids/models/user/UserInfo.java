package com.mycity4kids.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserInfo implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("dynamoId")
    private String dynamoId;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("email")
    private String email;
    @SerializedName("mobile_number")
    private String mobile_number;
    @SerializedName("name")
    private String name;
    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("profileId")
    private String profileId;
    @SerializedName("pincode")
    private String pincode;
    @SerializedName("family_id")
    private int family_id = 0;
    @SerializedName("color_code")
    private String color_code = "";
    @SerializedName("mc4kToken")
    private String mc4kToken;
    @SerializedName("isValidated")
    private String isValidated;
    @SerializedName("userType")
    private String userType;
    @SerializedName("profilePicUrl")
    private String profilePicUrl;
    @SerializedName("loginMode")
    private String loginMode;
    @SerializedName("cityId")
    private String cityId;
    @SerializedName("isLangSelection")
    private String isLangSelection;
    @SerializedName("subscriptionEmail")
    private String subscriptionEmail;
    @SerializedName("gender")
    private String gender;
    @SerializedName("blogTitle")
    private String blogTitle = "";
    @SerializedName("isNewUser")
    private String isNewUser = "";

    public UserInfo() {
        super();
    }

    public String getId() {
        return id;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDynamoId() {
        return dynamoId;
    }

    public void setDynamoId(String dynamoId) {
        this.dynamoId = dynamoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMc4kToken() {
        return mc4kToken;
    }

    public void setMc4kToken(String mc4kToken) {
        this.mc4kToken = mc4kToken;
    }

    public String getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(String isValidated) {
        this.isValidated = isValidated;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getIsLangSelection() {
        return isLangSelection;
    }

    public void setIsLangSelection(String isLangSelection) {
        this.isLangSelection = isLangSelection;
    }

    public String getSubscriptionEmail() {
        return subscriptionEmail;
    }

    public void setSubscriptionEmail(String subscriptionEmail) {
        this.subscriptionEmail = subscriptionEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    protected UserInfo(Parcel in) {
        id = in.readString();
        dynamoId = in.readString();
        mobile_number = in.readString();
        first_name = in.readString();
        name = in.readString();
        last_name = in.readString();
        email = in.readString();
        sessionId = in.readString();
        profileId = in.readString();
        pincode = in.readString();
        family_id = in.readInt();
        color_code = in.readString();
        mc4kToken = in.readString();
        isValidated = in.readString();
        userType = in.readString();
        profilePicUrl = in.readString();
        loginMode = in.readString();
        cityId = in.readString();
        isLangSelection = in.readString();
        subscriptionEmail = in.readString();
        gender = in.readString();
        blogTitle = in.readString();
        isNewUser = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(dynamoId);
        dest.writeString(mobile_number);
        dest.writeString(first_name);
        dest.writeString(name);
        dest.writeString(last_name);
        dest.writeString(email);
        dest.writeString(sessionId);
        dest.writeString(profileId);
        dest.writeString(pincode);
        dest.writeInt(family_id);
        dest.writeString(color_code);
        dest.writeString(mc4kToken);
        dest.writeString(isValidated);
        dest.writeString(userType);
        dest.writeString(profilePicUrl);
        dest.writeString(loginMode);
        dest.writeString(cityId);
        dest.writeString(isLangSelection);
        dest.writeString(subscriptionEmail);
        dest.writeString(gender);
        dest.writeString(blogTitle);
        dest.writeString(isNewUser);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

}
