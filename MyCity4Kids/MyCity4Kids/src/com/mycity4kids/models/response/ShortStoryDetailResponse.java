package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 27/3/17.
 */
public class ShortStoryDetailResponse extends BaseResponse {
    @SerializedName("data")
    private ShortStoryDetailResult data;

    public ShortStoryDetailResult getData() {
        return data;
    }

    public void setData(ShortStoryDetailResult data) {
        this.data = data;
    }
}
