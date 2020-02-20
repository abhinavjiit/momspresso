package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class AddGroupPostResponse extends BaseResponse {
    @SerializedName("data")
    private List<AddGroupPostData> data;

    public List<AddGroupPostData> getData() {
        return data;
    }

    public void setData(List<AddGroupPostData> data) {
        this.data = data;
    }

}

