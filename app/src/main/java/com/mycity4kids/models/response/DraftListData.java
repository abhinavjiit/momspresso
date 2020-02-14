package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anshul on 7/5/16.
 */
public class DraftListData extends BaseData {
    @SerializedName("result")
    private ArrayList<DraftListResult> result;

    public ArrayList<DraftListResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<DraftListResult> data) {
        this.result = data;
    }
}
