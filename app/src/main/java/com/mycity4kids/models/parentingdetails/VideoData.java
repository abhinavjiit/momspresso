package com.mycity4kids.models.parentingdetails;

import com.google.gson.annotations.SerializedName;

public class VideoData {
    @SerializedName("key")
    private String key;
    @SerializedName("videoUrl")
    private String videoUrl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
