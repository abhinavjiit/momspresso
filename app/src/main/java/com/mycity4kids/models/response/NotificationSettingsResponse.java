package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsResponse extends BaseResponse {
    @SerializedName("data")
    private NotificationSettingsData data;

    public NotificationSettingsData getData() {
        return data;
    }

    public void setData(NotificationSettingsData data) {
        this.data = data;
    }
}
