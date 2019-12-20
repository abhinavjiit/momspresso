package com.mycity4kids.models.parentingdetails;

import com.google.gson.annotations.SerializedName;

public class ImageData {
    @SerializedName("key")
    private String key;
    @SerializedName("clientApp")
    private String clientApp;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return clientApp;
    }

    public void setValue(String value) {
        this.clientApp = value;
    }

}
