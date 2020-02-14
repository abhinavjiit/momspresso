package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anshul on 7/4/16.
 */
public class ArticleDraftResponse extends BaseResponse {

    @SerializedName("data")
    private List<ArticleDraftData> data;

    public List<ArticleDraftData> getData() {
        return data;
    }

    public void setData(List<ArticleDraftData> data) {
        this.data = data;
    }
}
