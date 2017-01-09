package com.mycity4kids.models.response;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsDetailResponse extends BaseResponse {
    private VlogsDetailData data;

    public VlogsDetailData getData() {
        return data;
    }

    public void setData(VlogsDetailData data) {
        this.data = data;
    }
}
