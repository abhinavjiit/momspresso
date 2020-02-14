package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListResponse extends BaseResponse {

    @SerializedName("data")
    private NotificationCenterData data;

    public NotificationCenterData getData() {
        return data;
    }

    public void setData(NotificationCenterData data) {
        this.data = data;
    }
}
