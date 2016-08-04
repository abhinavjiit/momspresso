package com.mycity4kids.models.response;

/**
 * Created by hemant on 2/8/16.
 */
public class FollowUnfollowUserData {

    private String msg;
    private FollowUnfollowUserResult result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public FollowUnfollowUserResult getResult() {
        return result;
    }

    public void setResult(FollowUnfollowUserResult result) {
        this.result = result;
    }
}
