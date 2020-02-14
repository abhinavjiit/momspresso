package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupsActionVoteData {
    @SerializedName("result")
    private List<GroupsActionVoteResult> result;

    public List<GroupsActionVoteResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsActionVoteResult> result) {
        this.result = result;
    }

}