package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

public class UpdateVlogTitleRequest {
    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

