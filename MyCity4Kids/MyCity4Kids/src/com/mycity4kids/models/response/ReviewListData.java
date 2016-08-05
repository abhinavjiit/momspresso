package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by anshul on 7/29/16.
 */
public class ReviewListData extends BaseData {
    public ArrayList<ReviewListingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ReviewListingResult> result) {
        this.result = result;
    }

    ArrayList<ReviewListingResult> result;
}
