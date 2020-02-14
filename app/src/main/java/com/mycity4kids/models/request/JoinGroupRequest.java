package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by hemant on 30/4/18.
 */

public class JoinGroupRequest {

    @SerializedName("groupId")
    private int groupId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("questionnaireResponse")
    private Map<String, String> questionnaireResponse;

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

    public Map<String, String> getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(Map<String, String> questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }
}
