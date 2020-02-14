package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/11/16.
 */
public class BlogPageResponse extends BaseResponse {
    @SerializedName("data")
    private BlogPageData data;

    public BlogPageData getData() {
        return data;
    }

    public void setData(BlogPageData data) {
        this.data = data;
    }
}
