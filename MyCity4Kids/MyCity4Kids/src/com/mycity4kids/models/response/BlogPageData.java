package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 7/11/16.
 */
public class BlogPageData extends BaseData{
    @SerializedName("result")
    private BlogPageResult result;

    public BlogPageResult getResult() {
        return result;
    }

    public void setResult(BlogPageResult result) {
        this.result = result;
    }
}
