package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailData {

    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private UserDetailResult result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserDetailResult getResult() {
        return result;
    }

    public void setResult(UserDetailResult result) {
        this.result = result;
    }
}
