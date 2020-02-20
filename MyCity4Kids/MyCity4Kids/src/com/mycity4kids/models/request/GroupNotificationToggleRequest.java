package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 6/7/18.
 */

public class GroupNotificationToggleRequest {
    @SerializedName("notificationOn")
    private int notificationOn;

    public int getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(int notificationOn) {
        this.notificationOn = notificationOn;
    }
}
