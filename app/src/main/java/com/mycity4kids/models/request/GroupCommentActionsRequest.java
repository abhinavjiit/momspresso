package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 22/5/18.
 */

public class GroupCommentActionsRequest {

    @SerializedName("userId")
    private String userId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("postId")
    private int postId;
    @SerializedName("responseId")
    private int responseId;

    // {'0': 'helpfull', '1': 'nothelpfull', '2': 'share', '3': 'vote', '4': 'tag'}
    @SerializedName("type")
    private String type;
    @SerializedName("voteOption")
    private String voteOption;

    // {'0': '1', '1': 'fb', '2': 'insta', '3': 'twitter', '4': 'whatsapp', '5': 'others'}
    @SerializedName("sharedOn")
    private String sharedOn;
    @SerializedName("taggedUserId")
    private String taggedUserId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
