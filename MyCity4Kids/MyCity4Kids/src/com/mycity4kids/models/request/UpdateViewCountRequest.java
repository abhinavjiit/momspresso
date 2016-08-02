package com.mycity4kids.models.request;

import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 28/7/16.
 */
public class UpdateViewCountRequest {
    private String userId;
    private List<Map<String, String>> tags;
    private List<Map<String, String>> cities;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
