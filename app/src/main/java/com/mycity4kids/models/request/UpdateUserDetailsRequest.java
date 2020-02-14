package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 28/12/16.
 */
public class UpdateUserDetailsRequest {

    @SerializedName("attributeName")
    private String attributeName;
    @SerializedName("attributeValue")
    private String attributeValue;
    @SerializedName("attributeType")
    private String attributeType;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("userBio")
    private String userBio;
    @SerializedName("cityId")
    private String cityId;
    @SerializedName("cityName")
    private String cityName;
    @SerializedName("blogTitle")
    private String blogTitle;
    @SerializedName("subscriptionEmail")
    private String subscriptionEmail;
    @SerializedName("isValidated")
    private String isValidated;
    @SerializedName("kids")
    private ArrayList<AddRemoveKidsRequest> kids;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getSubscriptionEmail() {
        return subscriptionEmail;
    }

    public void setSubscriptionEmail(String subscriptionEmail) {
        this.subscriptionEmail = subscriptionEmail;
    }

    public String getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(String isValidated) {
        this.isValidated = isValidated;
    }

    public ArrayList<AddRemoveKidsRequest> getKids() {
        return kids;
    }

    public void setKids(ArrayList<AddRemoveKidsRequest> kids) {
        this.kids = kids;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
