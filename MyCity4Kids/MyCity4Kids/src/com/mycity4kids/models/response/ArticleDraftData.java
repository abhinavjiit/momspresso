package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/5/16.
 */
public class ArticleDraftData extends BaseData {
    @SerializedName("result")
    private ArticleDraftResult result;

    public ArticleDraftResult getResult() {
        return result;
    }

    public void setResult(ArticleDraftResult result) {
        this.result = result;
    }
}
