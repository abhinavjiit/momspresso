package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/23/16.
 */
public class DeepLinkingResposnse extends BaseResponse {
    public DeepLinkingData getData() {
        return data;
    }

    public void setData(DeepLinkingData data) {
        this.data = data;
    }

    DeepLinkingData data;
}
