package com.mycity4kids.models.response;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsResponse extends BaseResponse {

    private NotificationSettingsData data;

    public NotificationSettingsData getData() {
        return data;
    }

    public void setData(NotificationSettingsData data) {
        this.data = data;
    }
}
