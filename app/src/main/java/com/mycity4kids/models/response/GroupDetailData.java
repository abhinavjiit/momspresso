package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 1/5/18.
 */

public class GroupDetailData extends BaseData {

    @SerializedName("result")
    private GroupResult result;

    public GroupResult getResult() {
        return result;
    }

    public void setResult(GroupResult result) {
        this.result = result;
    }
}
