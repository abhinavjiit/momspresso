package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class GroupResult implements Parcelable {
    private String id;
    private String title;
    private String logoImage;
    private String headerImage;
    private String description;
    private String type;
    private String url;
    private String createdBy;
    private String createdOn;
    private String hashId;
    private String lang;
    private long createdAt;
    private long updatedAt;
    private Map<String, String> questionnaire;

    protected GroupResult(Parcel in) {
        id = in.readString();
        title = in.readString();
        logoImage = in.readString();
        headerImage = in.readString();
        description = in.readString();
        type = in.readString();
        url = in.readString();
        createdBy = in.readString();
        createdOn = in.readString();
        hashId = in.readString();
        lang = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    public static final Creator<GroupResult> CREATOR = new Creator<GroupResult>() {
        @Override
        public GroupResult createFromParcel(Parcel in) {
            return new GroupResult(in);
        }

        @Override
        public GroupResult[] newArray(int size) {
            return new GroupResult[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, String> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Map<String, String> questionnaire) {
        this.questionnaire = questionnaire;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(logoImage);
        dest.writeString(headerImage);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(createdBy);
        dest.writeString(createdOn);
        dest.writeString(hashId);
        dest.writeString(lang);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
    }
}
