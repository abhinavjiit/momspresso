package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/29/16.
 */
public class ReviewListData extends BaseData {
    public ReviewListingResult getResult() {
        return result;
    }

    public void setResult(ReviewListingResult result) {
        this.result = result;
    }

    ReviewListingResult result;
}
