package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigResult {
    public int getUserTypeVersion() {
        return userTypeVersion;
    }

    public void setUserTypeVersion(int userTypeVersion) {
        this.userTypeVersion = userTypeVersion;
    }

    int userTypeVersion;

    public int getArticleTypeVersion() {
        return articleTypeVersion;
    }

    public void setArticleTypeVersion(int articleTypeVersion) {
        this.articleTypeVersion = articleTypeVersion;
    }

    int articleTypeVersion;

    public ConfigCategory getCategory() {
        return category;
    }

    public void setCategory(ConfigCategory category) {
        this.category = category;
    }

    ConfigCategory category;
}
