package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 9/12/16.
 */
public class BloggerAnalyticsResponse extends BaseResponse {
    @SerializedName("data")
    private BloggerAnalyticsData data;

    public BloggerAnalyticsData getData() {
        return data;
    }

    public void setData(BloggerAnalyticsData data) {
        this.data = data;
    }
}
