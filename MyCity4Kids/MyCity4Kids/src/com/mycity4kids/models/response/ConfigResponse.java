package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/12/16.
 */

public class ConfigResponse extends BaseResponse {
    @SerializedName("data")
    private ConfigData data;

    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }
}
