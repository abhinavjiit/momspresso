package com.mycity4kids.models.request;

import java.util.Map;

/**
 * Created by hemant on 30/4/18.
 */

public class JoinGroupRequest {

    private int groupId;
    private String userId;
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
