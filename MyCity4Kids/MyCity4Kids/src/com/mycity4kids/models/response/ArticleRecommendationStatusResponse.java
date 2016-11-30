package com.mycity4kids.models.response;

/**
 * Created by hemant on 15/11/16.
 */
public class ArticleRecommendationStatusResponse extends BaseResponse {
    private TopicsFollowingStatusData data;

    public TopicsFollowingStatusData getData() {
        return data;
    }

    public void setData(TopicsFollowingStatusData data) {
        this.data = data;
    }
}
