package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupsListingData extends BaseData {

    @SerializedName("result")
    private List<GroupResult> result;

    public List<GroupResult> getResult() {
        return result;
    }

    public void setResult(List<GroupResult> result) {
        this.result = result;
    }

}
