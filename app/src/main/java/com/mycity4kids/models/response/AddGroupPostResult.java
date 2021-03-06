package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class AddGroupPostResult implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("content")
    private String content;
    @SerializedName("url")
    private String url;
    @SerializedName("type")
    private String type;
    @SerializedName("lang")
    private String lang;
    @SerializedName("isActive")
    private int isActive;
    @SerializedName("mediaUrls")
    private Object mediaUrls;
    @SerializedName("disableComments")
    private int disableComments;
    @SerializedName("isAnnon")
    private int isAnnon;
    @SerializedName("isPinned")
    private int isPinned;
    @SerializedName("moderatedBy")
    private String moderatedBy;
    @SerializedName("moderationStatus")
    private String moderationStatus;
    @SerializedName("moderationOn")
    private String moderationOn;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("pollType")
    private String pollType;
    @SerializedName("pollOptions")
    private Object pollOptions;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;

    protected AddGroupPostResult(Parcel in) {
        id = in.readString();
        content = in.readString();
        url = in.readString();
        type = in.readString();
        lang = in.readString();
        isActive = in.readInt();
        disableComments = in.readInt();
        isAnnon = in.readInt();
        isPinned = in.readInt();
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
        dest.writeInt(isActive);
        dest.writeInt(disableComments);
        dest.writeInt(isAnnon);
        dest.writeInt(isPinned);
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

    public int isActive() {
        return isActive;
    }

    public void setActive(int active) {
        isActive = active;
    }

    public int isDisableComments() {
        return disableComments;
    }

    public void setDisableComments(int disableComments) {
        this.disableComments = disableComments;
    }

    public int isAnnon() {
        return isAnnon;
    }

    public void setAnnon(int annon) {
        isAnnon = annon;
    }

    public int isPinned() {
        return isPinned;
    }

    public void setPinned(int pinned) {
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
