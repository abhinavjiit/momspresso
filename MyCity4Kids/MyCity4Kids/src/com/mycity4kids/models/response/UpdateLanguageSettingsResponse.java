package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 29/3/17.
 */
public class UpdateLanguageSettingsResponse extends BaseResponse {
    @SerializedName("data")
    private Map<String, String> data;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
