package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListResponse extends BaseResponse {
    @SerializedName("data")
    ContributorListData data;

    public ContributorListData getData() {
        return data;
    }

    public void setData(ContributorListData data) {
        this.data = data;
    }
}
