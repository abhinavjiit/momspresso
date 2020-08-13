package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateGroupMembershipRequest {

    @SerializedName("userId")
    private String userId;
    @SerializedName("status")
    private String status;
    @SerializedName("reason")
    private String reason;
    @SerializedName("questionnaireResponse")
    private Map<String, String> questionnaireResponse;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, String> getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(Map<String, String> questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }
}
