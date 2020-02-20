package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupsSettingData extends BaseData {

    @SerializedName("result")
    private ArrayList<GroupSettingResult> result;

    public ArrayList<GroupSettingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupSettingResult> result) {
        this.result = result;
    }

}
