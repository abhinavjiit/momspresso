package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/12/16.
 */
public class SetupBlogResponse extends BaseResponse {
SetupBlogData data;

    public SetupBlogData getData() {
        return data;
    }

    public void setData(SetupBlogData data) {
        this.data = data;
    }
}
