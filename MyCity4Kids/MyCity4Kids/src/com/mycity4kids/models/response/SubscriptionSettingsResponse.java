package com.mycity4kids.models.response;

/**
 * Created by hemant on 9/3/17.
 */
public class SubscriptionSettingsResponse extends BaseResponse {

    private SubscriptionSettingsData data;

    public SubscriptionSettingsData getData() {
        return data;
    }

    public void setData(SubscriptionSettingsData data) {
        this.data = data;
    }
}
