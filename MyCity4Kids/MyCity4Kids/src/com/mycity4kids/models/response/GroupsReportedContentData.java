package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 18/7/18.
 */

public class GroupsReportedContentData {

    @SerializedName("result")
    private ArrayList<GroupReportedContentResult> result;

    public ArrayList<GroupReportedContentResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupReportedContentResult> result) {
        this.result = result;
    }
}
