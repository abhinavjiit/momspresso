package com.mycity4kids.models.request;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateUsersGpLevelNotificationSettingRequest {
    private String userId;
    private int postId;
    private int groupId;
    private int isBookmarked;
    private int isAnno;
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
