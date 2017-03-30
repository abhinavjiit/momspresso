package com.mycity4kids.models.response;

/**
 * Created by hemant on 22/3/17.
 */
public class LanguageSettingsResponse extends BaseResponse {

    private LanguageSettingsData data;

    public LanguageSettingsData getData() {
        return data;
    }

    public void setData(LanguageSettingsData data) {
        this.data = data;
    }
}
