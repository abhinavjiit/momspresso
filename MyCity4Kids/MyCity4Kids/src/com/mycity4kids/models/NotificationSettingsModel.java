package com.mycity4kids.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 7/12/16.
 */
public class NotificationSettingsModel {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private String status;

    public NotificationSettingsModel() {
    }

    public NotificationSettingsModel(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
