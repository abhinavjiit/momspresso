package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 24/5/18.
 */

public class AddGpPostCommentOrReplyRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("content")
    private String content;
    @SerializedName("parentId")
    private int parentId;
    @SerializedName("isAnnon")
    private int isAnnon;
    @SerializedName("mediaUrls")
    private Map<String, String> mediaUrls;

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

    public Map<String, String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(Map<String, String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getIsAnnon() {
        return isAnnon;
    }

    public void setIsAnnon(int isAnnon) {
        this.isAnnon = isAnnon;
    }
}
