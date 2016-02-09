package com.mycity4kids.newmodels;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by hemant on 1/2/16.
 */
public class UserInviteResponse extends BaseDataModel {
    private int responseCode;
    private String response;
    private UserInviteResult result;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public UserInviteResult getResult() {
        return result;
    }

    public void setResult(UserInviteResult result) {
        this.result = result;
    }
}
