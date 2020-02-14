package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class UserPostSettingData extends BaseData {

    @SerializedName("result")
    private List<UserPostSettingResult> result;

    public List<UserPostSettingResult> getResult() {
        return result;
    }

    public void setResult(List<UserPostSettingResult> result) {
        this.result = result;
    }

}
