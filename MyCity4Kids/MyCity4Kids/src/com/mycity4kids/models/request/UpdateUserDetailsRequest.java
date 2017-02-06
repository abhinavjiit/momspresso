package com.mycity4kids.models.request;

import com.mycity4kids.models.response.KidsModel;

import java.util.ArrayList;

/**
 * Created by hemant on 28/12/16.
 */
public class UpdateUserDetailsRequest {

    private String attributeName;
    private String attributeValue;
    private String attributeType;
    private String firstName;
    private String lastName;
    private String mobile;
    private String userBio;
    private String cityId;
    private String cityName;
    private String blogTitle;
    private ArrayList<KidsModel> kids;

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

    public ArrayList<KidsModel> getKids() {
        return kids;
    }

    public void setKids(ArrayList<KidsModel> kids) {
        this.kids = kids;
    }
}
