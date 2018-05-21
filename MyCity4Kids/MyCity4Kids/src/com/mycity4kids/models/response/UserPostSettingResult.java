package com.mycity4kids.models.response;

/**
 * Created by hemant on 1/5/18.
 */

public class UserPostSettingResult {
    private int id;
    private String userId;
    private String postId;
    private int notificationOff;
    private int isBookmarked;
    private int isAnno;
    private String createdAt;
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getNotificationOff() {
        return notificationOff;
    }

    public void setNotificationOff(int notificationOff) {
        this.notificationOff = notificationOff;
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
