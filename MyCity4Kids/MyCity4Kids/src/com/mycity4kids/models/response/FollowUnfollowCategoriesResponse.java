package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 15/11/16.
 */
public class FollowUnfollowCategoriesResponse extends BaseResponse {
    @SerializedName("data")
    private List<String> data = new ArrayList<String>();

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
