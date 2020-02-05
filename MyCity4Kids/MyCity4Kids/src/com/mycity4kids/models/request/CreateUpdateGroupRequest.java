package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 6/7/18.
 */

public class CreateUpdateGroupRequest {
    @SerializedName("title")
    private String title;
    @SerializedName("logoImage")
    private String logoImage;
    @SerializedName("headerImage")
    private String headerImage;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("lang")
    private String lang;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("userId")
    private String userId;
    @SerializedName("questionnaire")
    private Map<String, String> questionnaire;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Map<String, String> questionnaire) {
        this.questionnaire = questionnaire;
    }
}
