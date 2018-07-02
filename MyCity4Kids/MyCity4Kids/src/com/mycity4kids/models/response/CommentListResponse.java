package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 6/6/18.
 */

public class CommentListResponse extends BaseResponse {

    private List<CommentListData> data;
    private int count;

    public List<CommentListData> getData() {
        return data;
    }

    public void setData(List<CommentListData> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
