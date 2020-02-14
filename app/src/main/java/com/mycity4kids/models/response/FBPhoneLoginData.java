package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class FBPhoneLoginData {

    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<UserDetailResult> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<UserDetailResult> getResult() {
        return result;
    }

    public void setResult(List<UserDetailResult> result) {
        this.result = result;
    }
}
