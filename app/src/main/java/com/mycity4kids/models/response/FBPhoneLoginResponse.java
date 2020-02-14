package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class FBPhoneLoginResponse extends BaseResponse {
    @SerializedName("data")
    private List<FBPhoneLoginData> data;

    public List<FBPhoneLoginData> getData() {
        return data;
    }

    public void setData(List<FBPhoneLoginData> data) {
        this.data = data;
    }

}
