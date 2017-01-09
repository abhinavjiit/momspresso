package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 4/1/17.
 */
public class VlogsListingData {
    private ArrayList<VlogsListingAndDetailResult> result;

    public ArrayList<VlogsListingAndDetailResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<VlogsListingAndDetailResult> result) {
        this.result = result;
    }
}
