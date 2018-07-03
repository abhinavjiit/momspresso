package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupPostCommentResult implements Parcelable {
    private int id;
    private String content;
    private String sentiment;
    private int parentId;
    private int groupId;
    private int postId;
    private String userId;
    private int isActive;
    private int isAnnon;
    private String moderationStatus;
    private String moderatedBy;
    private String moderatedOn;
    private String lang;
    private long createdAt;
    private long updatedAt;
    private ArrayList<GroupPostCommentResult> childData;
    private int isLastConversation = 0;
    private int childCount;

    public GroupPostCommentResult() {

    }

    protected GroupPostCommentResult(Parcel in) {
        id = in.readInt();
        content = in.readString();
        sentiment = in.readString();
        parentId = in.readInt();
        groupId = in.readInt();
        postId = in.readInt();
        userId = in.readString();
        isActive = in.readInt();
        isAnnon = in.readInt();
        moderationStatus = in.readString();
        moderatedBy = in.readString();
        moderatedOn = in.readString();
        lang = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        childData = in.createTypedArrayList(GroupPostCommentResult.CREATOR);
        isLastConversation = in.readInt();
    }

    public static final Creator<GroupPostCommentResult> CREATOR = new Creator<GroupPostCommentResult>() {
        @Override
        public GroupPostCommentResult createFromParcel(Parcel in) {
            return new GroupPostCommentResult(in);
        }

        @Override
        public GroupPostCommentResult[] newArray(int size) {
            return new GroupPostCommentResult[size];
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

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsAnnon() {
        return isAnnon;
    }

    public void setIsAnnon(int isAnnon) {
        this.isAnnon = isAnnon;
    }

    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public String getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(String moderatedBy) {
        this.moderatedBy = moderatedBy;
    }

    public String getModeratedOn() {
        return moderatedOn;
    }

    public void setModeratedOn(String moderatedon) {
        this.moderatedOn = moderatedon;
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

    public ArrayList<GroupPostCommentResult> getChildData() {
        return childData;
    }

    public void setChildData(ArrayList<GroupPostCommentResult> childData) {
        this.childData = childData;
    }

    public int getIsLastConversation() {
        return isLastConversation;
    }

    public void setIsLastConversation(int isLastConversation) {
        this.isLastConversation = isLastConversation;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(sentiment);
        dest.writeInt(parentId);
        dest.writeInt(groupId);
        dest.writeInt(postId);
        dest.writeString(userId);
        dest.writeInt(isActive);
        dest.writeInt(isAnnon);
        dest.writeString(moderationStatus);
        dest.writeString(moderatedBy);
        dest.writeString(moderatedOn);
        dest.writeString(lang);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeTypedList(childData);
        dest.writeInt(isLastConversation);
    }
}