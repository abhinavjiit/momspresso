package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupsReportContentData {
    @SerializedName("result")
    private List<GroupsReportContentResult> result;

    public List<GroupsReportContentResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsReportContentResult> result) {
        this.result = result;
    }

}