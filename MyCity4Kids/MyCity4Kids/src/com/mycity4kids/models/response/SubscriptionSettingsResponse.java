package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 9/3/17.
 */
public class SubscriptionSettingsResponse extends BaseResponse {
    @SerializedName("data")
    private SubscriptionSettingsData data;

    public SubscriptionSettingsData getData() {
        return data;
    }

    public void setData(SubscriptionSettingsData data) {
        this.data = data;
    }
}
