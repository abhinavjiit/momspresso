package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/10/16.
 */
public class ForgotPasswordResponse extends BaseResponse {
    @SerializedName("data")
    private List<ForgotPasswordData> data;

    public List<ForgotPasswordData> getData() {
        return data;
    }

    public void setData(List<ForgotPasswordData> data) {
        this.data = data;
    }
}
