package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class AddGroupPostResponse extends BaseResponse {

    private List<AddGroupPostData> data;

    public List<AddGroupPostData> getData() {
        return data;
    }

    public void setData(List<AddGroupPostData> data) {
        this.data = data;
    }

}

