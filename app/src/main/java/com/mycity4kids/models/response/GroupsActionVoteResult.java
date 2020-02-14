package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupsActionVoteResult {
    @SerializedName("id")
    private int id;
    @SerializedName("sharedOn")
    private String sharedOn;
    @SerializedName("taggedUserId")
    private String taggedUserId;
    @SerializedName("responseId")
    private int responseId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("type")
    private String type;
    @SerializedName("voteOption")
    private String voteOption;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSharedOn() {
        return sharedOn;
    }

    public void setSharedOn(String sharedOn) {
        this.sharedOn = sharedOn;
    }

    public String getTaggedUserId() {
        return taggedUserId;
    }

    public void setTaggedUserId(String taggedUserId) {
        this.taggedUserId = taggedUserId;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(String voteOption) {
        this.voteOption = voteOption;
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
