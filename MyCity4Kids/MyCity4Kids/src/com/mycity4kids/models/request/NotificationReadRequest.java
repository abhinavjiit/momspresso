package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 6/2/17.
 */
public class NotificationReadRequest {
    @SerializedName("notifId")
    private String notifId;
    @SerializedName("readAll")
    private String readAll;

    public String getNotifId() {
        return notifId;
    }

    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }

    public String getReadAll() {
        return readAll;
    }

    public void setReadAll(String readAll) {
        this.readAll = readAll;
    }
}
