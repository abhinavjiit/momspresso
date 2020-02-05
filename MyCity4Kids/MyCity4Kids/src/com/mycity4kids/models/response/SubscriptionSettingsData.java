package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

/**
 * Created by hemant on 9/3/17.
 */
public class SubscriptionSettingsData {
    @SerializedName("result")
    private Map<String, Object> result;

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
