package com.mycity4kids.models.request;

/**
 * Created by hemant on 1/5/18.
 */

public class UpdateGroupMembershipRequest {
    private String userId;
    private String status;
    private String reason;

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
}