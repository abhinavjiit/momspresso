package com.mycity4kids.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/6/16.
 */
public class LoginRegistrationRequest {

    @SerializedName("cityId")
    private String cityId;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("requestMedium")
    private String requestMedium;
    @SerializedName("socialToken")
    private String socialToken;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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

    public String getRequestMedium() {
        return requestMedium;
    }

    public void setRequestMedium(String requestMedium) {
        this.requestMedium = requestMedium;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public void setSocialToken(String socialToken) {
        this.socialToken = socialToken;
    }
}
