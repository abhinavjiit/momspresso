package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersFollowingData {
    private String msg;
    private ArrayList<FollowersFollowingResult> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<FollowersFollowingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<FollowersFollowingResult> result) {
        this.result = result;
    }
}
