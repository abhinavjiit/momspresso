package com.mycity4kids.models.request;

import java.util.HashMap;

/**
 * Created by hemant on 9/3/17.
 */
public class SubscriptionUpdateRequest {
    private String email;

    private HashMap<String, String> subscribe;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, String> getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(HashMap<String, String> subscribe) {
        this.subscribe = subscribe;
    }
}
