package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by hemant on 19/7/18.
 */

public class GroupsJoinData {
    @SerializedName("result")
    private List<GroupsJoinResult> result;

    public List<GroupsJoinResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsJoinResult> result) {
        this.result = result;
    }
}
