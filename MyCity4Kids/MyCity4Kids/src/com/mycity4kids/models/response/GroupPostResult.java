package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by hemant on 1/5/18.
 */

public class GroupPostResult extends BaseResponse implements Parcelable {

    private int id;
    private String content;
    private String url;
    private String type;
    private String lang;
    private int isActive;
    private Object mediaUrls;
    private int disableComments;
    private int isAnnon;
    private String pinnedBy;
    private int isPinned;
    private String moderatedBy;
    private String moderationStatus;
    private String moderationOn;
    private String createdOn;
    private int groupId;
    private String userId;
    private long createdAt;
    private long updatedAt;
    private String pollType;
    private Object pollOptions;
    private boolean isVoted;

    public GroupPostResult() {
    }

    protected GroupPostResult(Parcel in) {
        id = in.readInt();
        content = in.readString();
        url = in.readString();
        type = in.readString();
        lang = in.readString();
        isActive = in.readInt();
        disableComments = in.readInt();
        isAnnon = in.readInt();
        pinnedBy = in.readString();
        isPinned = in.readInt();
        moderatedBy = in.readString();
        moderationStatus = in.readString();
        moderationOn = in.readString();
        createdOn = in.readString();
        groupId = in.readInt();
        userId = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        pollType = in.readString();
    }

    public static final Creator<GroupPostResult> CREATOR = new Creator<GroupPostResult>() {
        @Override
        public GroupPostResult createFromParcel(Parcel in) {
            return new GroupPostResult(in);
        }

        @Override
        public GroupPostResult[] newArray(int size) {
            return new GroupPostResult[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Object getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(Object mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public int getDisableComments() {
        return disableComments;
    }

    public void setDisableComments(int disableComments) {
        this.disableComments = disableComments;
    }

    public int getIsAnnon() {
        return isAnnon;
    }

    public void setIsAnnon(int isAnnon) {
        this.isAnnon = isAnnon;
    }

    public String getPinnedBy() {
        return pinnedBy;
    }

    public void setPinnedBy(String pinnedBy) {
        this.pinnedBy = pinnedBy;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeString(lang);
        dest.writeInt(isActive);
        dest.writeInt(disableComments);
        dest.writeInt(isAnnon);
        dest.writeString(pinnedBy);
        dest.writeInt(isPinned);
        dest.writeString(moderatedBy);
        dest.writeString(moderationStatus);
        dest.writeString(moderationOn);
        dest.writeString(createdOn);
        dest.writeInt(groupId);
        dest.writeString(userId);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeString(pollType);
    }
}

