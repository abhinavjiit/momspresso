package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GroupsActionData {
    @SerializedName("result")
    private List<GroupsActionResult> result;

    public List<GroupsActionResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsActionResult> result) {
        this.result = result;
    }

}