package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 12/10/16.
 */
public class ChangePasswordResponse extends BaseResponse {
    @SerializedName("data")
    private List<ChangePasswordData> data;

    public List<ChangePasswordData> getData() {
        return data;
    }

    public void setData(List<ChangePasswordData> data) {
        this.data = data;
    }
}
