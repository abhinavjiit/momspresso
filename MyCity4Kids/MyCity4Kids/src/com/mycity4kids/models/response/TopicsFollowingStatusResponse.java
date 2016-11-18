package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 15/11/16.
 */
public class TopicsFollowingStatusResponse extends BaseResponse {
    private TopicsFollowingStatusData data;

    public TopicsFollowingStatusData getData() {
        return data;
    }

    public void setData(TopicsFollowingStatusData data) {
        this.data = data;
    }
}
