package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class ConfigResult {

    int userTypeVersion;
    int articleTypeVersion;
    ConfigCategory category;

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


}
