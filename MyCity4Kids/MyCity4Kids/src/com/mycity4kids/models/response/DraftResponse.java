package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/4/16.
 */
public class DraftResponse extends BaseResponse {

DraftData data;

    public DraftData getData() {
        return data;
    }

    public void setData(DraftData data) {
        this.data = data;
    }
}
