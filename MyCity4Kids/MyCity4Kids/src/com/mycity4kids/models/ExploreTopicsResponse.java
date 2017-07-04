package com.mycity4kids.models;

import java.util.ArrayList;

/**
 * Created by hemant on 1/6/16.
 */
public class ExploreTopicsResponse {

    private String status;
    private int code;
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
