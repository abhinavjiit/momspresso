package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsData {
    @SerializedName("result")
    private Map<String, String> result;

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}
