package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by hemant on 22/3/17.
 */
public class LanguageSettingsData {
    @SerializedName("msg")
    private String msg;

    @SerializedName("result")
    private List<Map<String, String>> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Map<String, String>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, String>> result) {
        this.result = result;
    }
}
