package com.mycity4kids.models.response;

/**
 * Created by hemant on 27/3/17.
 */
public class ShortStoryDetailResponse extends BaseResponse {
    private ShortStoryDetailResult data;

    public ShortStoryDetailResult getData() {
        return data;
    }

    public void setData(ShortStoryDetailResult data) {
        this.data = data;
    }
}
