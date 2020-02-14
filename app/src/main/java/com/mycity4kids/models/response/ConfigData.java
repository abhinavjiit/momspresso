package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/12/16.
 */

public class ConfigData extends BaseData {
    @SerializedName("result")
    private ConfigResult result;

    public ConfigResult getResult() {
        return result;
    }

    public void setResult(ConfigResult result) {
        this.result = result;
    }
}
