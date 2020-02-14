package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 8/23/16.
 */
public class DeepLinkingResposnse extends BaseResponse {
    @SerializedName("data")
    private DeepLinkingData data;

    public DeepLinkingData getData() {
        return data;
    }

    public void setData(DeepLinkingData data) {
        this.data = data;
    }
}
