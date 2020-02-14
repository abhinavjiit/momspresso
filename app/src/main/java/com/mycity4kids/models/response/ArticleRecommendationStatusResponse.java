package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 15/11/16.
 */
public class ArticleRecommendationStatusResponse extends BaseResponse {
    @SerializedName("data")
    private TopicsFollowingStatusData data;

    public TopicsFollowingStatusData getData() {
        return data;
    }

    public void setData(TopicsFollowingStatusData data) {
        this.data = data;
    }
}
