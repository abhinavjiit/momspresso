package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/12/16.
 */
public class SetupBlogData {
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private SetupBlogResult result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SetupBlogResult getResult() {
        return result;
    }

    public void setResult(SetupBlogResult result) {
        this.result = result;
    }
}
