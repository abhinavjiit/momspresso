package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.parentingdetails.CommentsData;

import java.util.ArrayList;

/**
 * Created by hemant on 26/9/16.
 */
public class FBCommentData {
    @SerializedName("result")
    private ArrayList<CommentsData> result;
    @SerializedName("msg")
    private String msg;
    @SerializedName("pagination")
    private String pagination;

    public ArrayList<CommentsData> getResult() {
        return result;
    }

    public void setResult(ArrayList<CommentsData> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }
}
