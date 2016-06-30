package com.mycity4kids.models.response;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailData {
    private String id;
    private String sqlId;
    private String mc4kToken;
    private String firstName;
    private String lastName;
    private String email;
    private String cityId;
    private String userType;
    private String isValidated;
    private String profilePicUrl;

    public String getId() {
        return sqlId;
    }

    public void setId(String id) {
        this.sqlId = sqlId;
    }

    public String getDynamoId() {
        return id;
    }

    public void setDynamoId(String dynamoId) {
        this.id = id;
    }

    public String getMc4kToken() {
        return mc4kToken;
    }

    public void setMc4kToken(String mc4kToken) {
        this.mc4kToken = mc4kToken;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(String isValidated) {
        this.isValidated = isValidated;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
