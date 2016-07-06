package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/5/16.
 */
public class DraftListResponse extends BaseResponse {
DraftListData data;

    public void setData(DraftListData data) {
        this.data = data;
    }

    public DraftListData getData() {
        return data;
    }
}
