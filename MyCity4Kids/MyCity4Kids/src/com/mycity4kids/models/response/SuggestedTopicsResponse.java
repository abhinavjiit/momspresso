package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsResponse extends BaseResponse {
    private List<SuggestedTopicsData> data;

    public List<SuggestedTopicsData> getData() {
        return data;
    }

    public void setData(List<SuggestedTopicsData> data) {
        this.data = data;
    }
}
