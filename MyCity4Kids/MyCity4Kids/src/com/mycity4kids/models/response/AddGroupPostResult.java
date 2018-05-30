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
    private boolean isActive;
    private Object mediaUrls;
    private boolean disableComments;
    private boolean isAnnon;
    private boolean isPinned;
    private String moderatedBy;
    private String moderationStatus;
    private String moderationOn;
    private String createdOn;
    private String groupId;
    private String userId;
    private String pollType;
    private Object pollOptions;
    private long createdAt;
    private long updatedAt;


    protected AddGroupPostResult(Parcel in) {
        id = in.readString();
        content = in.readString();
        url = in.readString();
        type = in.readString();
        lang = in.readString();
        isActive = in.readByte() != 0;
        disableComments = in.readByte() != 0;
        isAnnon = in.readByte() != 0;
        isPinned = in.readByte() != 0;
        moderatedBy = in.readString();
        moderationStatus = in.readString();
        moderationOn = in.readString();
        createdOn = in.readString();
        groupId = in.readString();
        userId = in.readString();
        pollType = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeString(lang);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (disableComments ? 1 : 0));
        dest.writeByte((byte) (isAnnon ? 1 : 0));
        dest.writeByte((byte) (isPinned ? 1 : 0));
        dest.writeString(moderatedBy);
        dest.writeString(moderationStatus);
        dest.writeString(moderationOn);
        dest.writeString(createdOn);
        dest.writeString(groupId);
        dest.writeString(userId);
        dest.writeString(pollType);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
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

    public Object getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(Object mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDisableComments() {
        return disableComments;
    }

    public void setDisableComments(boolean disableComments) {
        this.disableComments = disableComments;
    }

    public boolean isAnnon() {
        return isAnnon;
    }

    public void setAnnon(boolean annon) {
        isAnnon = annon;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
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
}
