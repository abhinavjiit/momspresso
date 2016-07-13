package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigResponse extends BaseResponse {
    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }

    ConfigData data;

}
