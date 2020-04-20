package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MomVlogersDetailData {
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private ArrayList<UserDetailResult> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<UserDetailResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserDetailResult> result) {
        this.result = result;
    }
}
