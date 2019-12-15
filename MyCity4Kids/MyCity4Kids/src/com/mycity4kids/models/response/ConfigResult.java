package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigResult {

    int userTypeVersion;
    int articleTypeVersion;
    ConfigCategory category;
    private Map<String, String> notificationSettings;
    private ArrayList<String> notificationType;
    private Map<String, String> language;
    private Map<String, LanguageConfigModel> languages;
    private String homeCarouselUrl = "";

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

    public ArrayList<String> getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(ArrayList<String> notificationType) {
        this.notificationType = notificationType;
    }

    public Map<String, String> getLanguage() {
        return language;
    }

    public void setLanguage(Map<String, String> language) {
        this.language = language;
    }

    public Map<String, LanguageConfigModel> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, LanguageConfigModel> languages) {
        this.languages = languages;
    }

    public String getHomeCarouselUrl() {
        return homeCarouselUrl;
    }

    public void setHomeCarouselUrl(String homeCarouselUrl) {
        this.homeCarouselUrl = homeCarouselUrl;
    }
}
