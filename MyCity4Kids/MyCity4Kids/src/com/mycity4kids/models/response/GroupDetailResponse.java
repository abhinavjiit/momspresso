package com.mycity4kids.models.response;

/**
 * Created by hemant on 1/5/18.
 */

public class GroupDetailResponse extends BaseResponse {

    private GroupDetailData data;

    public GroupDetailData getData() {
        return data;
    }

    public void setData(GroupDetailData data) {
        this.data = data;
    }
}
