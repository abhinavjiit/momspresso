package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupPostData extends BaseData {
    @SerializedName("result")
    private List<GroupPostResult> result;

    public List<GroupPostResult> getResult() {
        return result;
    }

    public void setResult(List<GroupPostResult> result) {
        this.result = result;
    }

}
