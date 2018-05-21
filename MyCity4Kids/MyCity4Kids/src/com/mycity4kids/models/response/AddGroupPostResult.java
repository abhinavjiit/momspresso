package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class AddGroupPostResult implements Parcelable {
    private String id;
    private String content;
    private String url;
    private String type;
    private String lang;
    private String isActive;
    private Object mediaUrls;
    private String disableComments;
    private String isAnnon;
    private String isPinned;
    private String moderatedBy;
    private String moderationStatus;
    private String moderationOn;
    private String createdOn;
    private String groupId;
    private String userId;
    private String pollType;
    private Object pollOptions;
    private String createdAt;
    private String updatedAt;

    protected AddGroupPostResult(Parcel in) {
        id = in.readString();
        content = in.readString();
        url = in.readString();
        type = in.readString();
        lang = in.readString();
        isActive = in.readString();
        disableComments = in.readString();
        isAnnon = in.readString();
        isPinned = in.readString();
        moderatedBy = in.readString();
        moderationStatus = in.readString();
        moderationOn = in.readString();
        createdOn = in.readString();
        groupId = in.readString();
        userId = in.readString();
        pollType = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeString(lang);
        dest.writeString(isActive);
        dest.writeString(disableComments);
        dest.writeString(isAnnon);
        dest.writeString(isPinned);
        dest.writeString(moderatedBy);
        dest.writeString(moderationStatus);
        dest.writeString(moderationOn);
        dest.writeString(createdOn);
        dest.writeString(groupId);
        dest.writeString(userId);
        dest.writeString(pollType);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddGroupPostResult> CREATOR = new Creator<AddGroupPostResult>() {
        @Override
        public AddGroupPostResult createFromParcel(Parcel in) {
            return new AddGroupPostResult(in);
        }

        @Override
        public AddGroupPostResult[] newArray(int size) {
            return new AddGroupPostResult[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Object getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(Object mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getDisableComments() {
        return disableComments;
    }

    public void setDisableComments(String disableComments) {
        this.disableComments = disableComments;
    }

    public String getIsAnnon() {
        return isAnnon;
    }

    public void setIsAnnon(String isAnnon) {
        this.isAnnon = isAnnon;
    }

    public String getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(String isPinned) {
        this.isPinned = isPinned;
    }

    public String getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(String moderatedBy) {
        this.moderatedBy = moderatedBy;
    }

    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public String getModerationOn() {
        return moderationOn;
    }

    public void setModerationOn(String moderationOn) {
        this.moderationOn = moderationOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPollType() {
        return pollType;
    }

    public void setPollType(String pollType) {
        this.pollType = pollType;
    }

    public Object getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(Object pollOptions) {
        this.pollOptions = pollOptions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
