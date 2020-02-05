package com.mycity4kids.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 1/6/16.
 */
public class ExploreTopicsResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private ArrayList<ExploreTopicsModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<ExploreTopicsModel> getData() {
        return data;
    }

    public void setData(ArrayList<ExploreTopicsModel> data) {
        this.data = data;
    }
}
