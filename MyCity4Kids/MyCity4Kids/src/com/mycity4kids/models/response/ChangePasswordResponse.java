package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 12/10/16.
 */
public class ChangePasswordResponse extends BaseResponse {
    private List<ChangePasswordData> data;

    public List<ChangePasswordData> getData() {
        return data;
    }

    public void setData(List<ChangePasswordData> data) {
        this.data = data;
    }
}
