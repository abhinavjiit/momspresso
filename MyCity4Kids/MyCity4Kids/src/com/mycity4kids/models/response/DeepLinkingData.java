package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/23/16.
 */
public class DeepLinkingData extends BaseData {
    public DeepLinkingResult getResult() {
        return result;
    }

    public void setResult(DeepLinkingResult result) {
        this.result = result;
    }

    DeepLinkingResult result;

}
