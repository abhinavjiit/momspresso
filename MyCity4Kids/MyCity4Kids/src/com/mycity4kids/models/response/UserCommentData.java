package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anshul on 8/2/16.
 */
public class UserCommentData extends BaseData {
    @SerializedName("result")
    private ArrayList<UserCommentsResult> result;
    @SerializedName("pagination")
    private String pagination;

    public ArrayList<UserCommentsResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserCommentsResult> result) {
        this.result = result;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }
}
