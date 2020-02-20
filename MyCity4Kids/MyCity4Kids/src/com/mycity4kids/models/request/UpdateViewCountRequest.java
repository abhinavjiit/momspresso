package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 28/7/16.
 */
public class UpdateViewCountRequest {
    @SerializedName("userId")
    private String userId;
    //For Short Stories only
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("tags")
    private List<Map<String, String>> tags;
    @SerializedName("cities")
    private List<Map<String, String>> cities;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<Map<String, String>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, String>> tags) {
        this.tags = tags;
    }

    public List<Map<String, String>> getCities() {
        return cities;
    }

    public void setCities(List<Map<String, String>> cities) {
        this.cities = cities;
    }
}
