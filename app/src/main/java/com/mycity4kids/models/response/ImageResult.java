package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 8/6/16.
 */
public class ImageResult {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
