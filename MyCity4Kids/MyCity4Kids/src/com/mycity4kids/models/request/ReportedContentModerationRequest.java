package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 17/7/18.
 */

public class ReportedContentModerationRequest {
    @SerializedName("isModerated")
    private int isModerated;
    @SerializedName("actionResponse")
    private String actionResponse;
    @SerializedName("actionBy")
    private String actionBy;

    public int getIsModerated() {
        return isModerated;
    }

    public void setIsModerated(int isModerated) {
        this.isModerated = isModerated;
    }

    public String getActionResponse() {
        return actionResponse;
    }

    public void setActionResponse(String actionResponse) {
        this.actionResponse = actionResponse;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }
}