package com.mycity4kids.models.response;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListResponse extends BaseResponse {
    public ContributorListData getData() {
        return data;
    }

    public void setData(ContributorListData data) {
        this.data = data;
    }

    ContributorListData data;
}
