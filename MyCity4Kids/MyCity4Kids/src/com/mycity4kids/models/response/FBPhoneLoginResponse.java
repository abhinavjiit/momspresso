package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class FBPhoneLoginResponse extends BaseResponse {
    private List<FBPhoneLoginData> data;

    public List<FBPhoneLoginData> getData() {
        return data;
    }

    public void setData(List<FBPhoneLoginData> data) {
        this.data = data;
    }

}
