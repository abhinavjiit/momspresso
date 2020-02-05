package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 12/4/18.
 */

public class AddGroupPostData extends BaseData {
    @SerializedName("result")
    private AddGroupPostResult result;

    public AddGroupPostResult getResult() {
        return result;
    }

    public void setResult(AddGroupPostResult result) {
        this.result = result;
    }

}
