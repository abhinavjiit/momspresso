package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupsActionResult {
    private int id;
    private String sharedOn;
    private String taggedUserId;
    private int responseId;
    private int groupId;
    private int postId;
    private String userId;
    private int type;
    private int voteOption;
    private long createdAt;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(int voteOption) {
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
