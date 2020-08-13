package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Created by hemant on 25/4/18.
 */

public class GroupsJoinResult {

    @SerializedName("id")
    private int id;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("status")
    private String status;
    @SerializedName("lastActivityOn")
    private String lastActivityOn;
    @SerializedName("referedBy")
    private String referedBy;
    @SerializedName("questionnaireResponse")
    private Map<String, String> questionnaireResponse;
    @SerializedName("inviteCode")
    private String inviteCode;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("updatedAt")
    private long updatedAt;
    @SerializedName("userInfo")
    private UserDetailResult userInfo;
    @SerializedName("groupInfo")
    private GroupResult groupInfo;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastActivityOn() {
        return lastActivityOn;
    }

    public void setLastActivityOn(String lastActivityOn) {
        this.lastActivityOn = lastActivityOn;
    }

    public String getReferedBy() {
        return referedBy;
    }

    public void setReferedBy(String referedBy) {
        this.referedBy = referedBy;
    }

    public Map<String, String> getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(Map<String, String> questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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

    public UserDetailResult getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDetailResult userInfo) {
        this.userInfo = userInfo;
    }

    public GroupResult getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupResult groupInfo) {
        this.groupInfo = groupInfo;
    }
}
