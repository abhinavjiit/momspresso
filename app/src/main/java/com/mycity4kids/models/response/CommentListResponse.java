package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hemant on 6/6/18.
 */

public class CommentListResponse extends BaseResponse {
    @SerializedName("data")
    private List<CommentListData> data;
    @SerializedName("count")
    private int count;

    public List<CommentListData> getData() {
        return data;
    }

    public void setData(List<CommentListData> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
