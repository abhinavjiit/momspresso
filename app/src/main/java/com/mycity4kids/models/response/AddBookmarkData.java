package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 29/7/16.
 */
public class AddBookmarkData {
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private AddBookmarkResult result;
    @SerializedName("id")
    private String id;

    public String getArticleId() {
        return id;
    }

    public void setArticleId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AddBookmarkResult getResult() {
        return result;
    }

    public void setResult(AddBookmarkResult result) {
        this.result = result;
    }
}
