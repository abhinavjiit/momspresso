package com.mycity4kids.models.response;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResponse extends BaseResponse {
    private UserDetailData data;

    public UserDetailData getData() {
        return data;
    }

    public void setData(UserDetailData data) {
        this.data = data;
    }

}
