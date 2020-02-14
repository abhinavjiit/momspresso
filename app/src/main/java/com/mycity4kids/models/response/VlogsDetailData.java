package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsDetailData {
    @SerializedName("result")
    private VlogsListingAndDetailResult result;

    public VlogsListingAndDetailResult getResult() {
        return result;
    }

    public void setResult(VlogsListingAndDetailResult result) {
        this.result = result;
    }
}
