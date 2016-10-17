package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 14/10/16.
 */
public class ViewCountResponse extends BaseResponse {

    private List<ViewCountData> data;

    public List<ViewCountData> getData() {
        return data;
    }

    public void setData(List<ViewCountData> data) {
        this.data = data;
    }
}
