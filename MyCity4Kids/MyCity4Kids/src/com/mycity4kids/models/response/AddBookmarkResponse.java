package com.mycity4kids.models.response;

/**
 * Created by hemant on 29/7/16.
 */
public class AddBookmarkResponse extends BaseResponse {

    private AddBookmarkData data;

    public AddBookmarkData getData() {
        return data;
    }

    public void setData(AddBookmarkData data) {
        this.data = data;
    }
}
