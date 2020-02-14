package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupsCategoryMappingData {
    @SerializedName("result")
    private List<GroupsCategoryMappingResult> result;

    public List<GroupsCategoryMappingResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsCategoryMappingResult> result) {
        this.result = result;
    }

}