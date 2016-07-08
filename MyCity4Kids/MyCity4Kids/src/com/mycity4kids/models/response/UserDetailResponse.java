package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResponse extends BaseResponse {
    private List<UserDetailData> data;

    public List<UserDetailData> getData() {
        return data;
    }

    public void setData(List<UserDetailData> data) {
        this.data = data;
    }

}
