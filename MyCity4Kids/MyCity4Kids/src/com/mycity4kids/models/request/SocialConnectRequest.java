package com.mycity4kids.models.request;

/**
 * Created by hemant on 8/12/16.
 */
public class SocialConnectRequest {

    private String token;
    private String referer;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }
}
