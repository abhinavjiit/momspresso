package com.mycity4kids.models.response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigResult {

    int userTypeVersion;
    int articleTypeVersion;
    ConfigCategory category;
    private Map<String, String> notificationSettings;
    private Map<String, String> notificationType;

    public int getUserTypeVersion() {
        return userTypeVersion;
    }

    public void setUserTypeVersion(int userTypeVersion) {
        this.userTypeVersion = userTypeVersion;
    }

    public int getArticleTypeVersion() {
        return articleTypeVersion;
    }

    public void setArticleTypeVersion(int articleTypeVersion) {
        this.articleTypeVersion = articleTypeVersion;
    }

    public ConfigCategory getCategory() {
        return category;
    }

    public void setCategory(ConfigCategory category) {
        this.category = category;
    }

    public Map<String, String> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(Map<String, String> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public Map<String, String> getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Map<String, String> notificationType) {
        this.notificationType = notificationType;
    }
}
