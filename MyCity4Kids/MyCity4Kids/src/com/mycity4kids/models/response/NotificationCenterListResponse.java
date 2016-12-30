package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListResponse extends BaseResponse {

    private NotificationCenterData data;

    public NotificationCenterData getData() {
        return data;
    }

    public void setData(NotificationCenterData data) {
        this.data = data;
    }
}
