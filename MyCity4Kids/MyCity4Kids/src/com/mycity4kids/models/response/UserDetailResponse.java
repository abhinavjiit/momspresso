package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResponse extends BaseResponse {
    @SerializedName("data")
    private List<UserDetailData> data;

    public List<UserDetailData> getData() {
        return data;
    }

    public void setData(List<UserDetailData> data) {
        this.data = data;
    }

}
