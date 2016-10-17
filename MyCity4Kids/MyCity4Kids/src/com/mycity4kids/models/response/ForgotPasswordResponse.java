package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/10/16.
 */
public class ForgotPasswordResponse extends BaseResponse {
    private List<ForgotPasswordData> data;

    public List<ForgotPasswordData> getData() {
        return data;
    }

    public void setData(List<ForgotPasswordData> data) {
        this.data = data;
    }
}
