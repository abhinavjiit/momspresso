package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateUsersGpLevelNotificationSettingRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("isBookmarked")
    private int isBookmarked;
    @SerializedName("isAnno")
    private int isAnno;
    @SerializedName("notificationOff")
    private int notificationOff;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(int isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public int getIsAnno() {
        return isAnno;
    }

    public void setIsAnno(int isAnno) {
        this.isAnno = isAnno;
    }

    public int getNotificationOff() {
        return notificationOff;
    }

    public void setNotificationOff(int notificationOff) {
        this.notificationOff = notificationOff;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
