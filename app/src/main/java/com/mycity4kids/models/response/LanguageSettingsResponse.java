package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 22/3/17.
 */
public class LanguageSettingsResponse extends BaseResponse {
    @SerializedName("data")
    private LanguageSettingsData data;

    public LanguageSettingsData getData() {
        return data;
    }

    public void setData(LanguageSettingsData data) {
        this.data = data;
    }
}
