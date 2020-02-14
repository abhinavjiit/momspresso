package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 29/7/16.
 */
public class AddBookmarkResponse extends BaseResponse {

    @SerializedName("data")
    private AddBookmarkData data;

    public AddBookmarkData getData() {
        return data;
    }

    public void setData(AddBookmarkData data) {
        this.data = data;
    }
}
