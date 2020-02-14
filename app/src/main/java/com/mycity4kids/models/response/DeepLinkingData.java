package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 8/23/16.
 */
public class DeepLinkingData extends BaseData {
    @SerializedName("result")
    private DeepLinkingResult result;

    public DeepLinkingResult getResult() {
        return result;
    }

    public void setResult(DeepLinkingResult result) {
        this.result = result;
    }
}
