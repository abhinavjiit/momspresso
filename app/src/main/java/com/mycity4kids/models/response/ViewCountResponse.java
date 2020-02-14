package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 14/10/16.
 */
public class ViewCountResponse extends BaseResponse {
    @SerializedName("data")
    private List<ViewCountData> data;

    public List<ViewCountData> getData() {
        return data;
    }

    public void setData(List<ViewCountData> data) {
        this.data = data;
    }
}
