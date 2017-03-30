package com.mycity4kids.models;

/**
 * Created by hemant on 7/12/16.
 */
public class SubscriptionAndLanguageSettingsModel {
    private String id;
    private String name;
    private String displayName;
    private String status;

    public SubscriptionAndLanguageSettingsModel() {
    }

    public SubscriptionAndLanguageSettingsModel(String id, String name, String status) {
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
