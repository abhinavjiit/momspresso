package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigData extends BaseData {
    public ConfigResult getResult() {
        return result;
    }

    public void setResult(ConfigResult result) {
        this.result = result;
    }

    ConfigResult result;

}
