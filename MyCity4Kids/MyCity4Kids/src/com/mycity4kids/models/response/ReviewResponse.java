package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/29/16.
 */
public class ReviewResponse extends BaseResponse {
    public ReviewListData getData() {
        return data;
    }

    public void setData(ReviewListData data) {
        this.data = data;
    }

    ReviewListData data;

}
