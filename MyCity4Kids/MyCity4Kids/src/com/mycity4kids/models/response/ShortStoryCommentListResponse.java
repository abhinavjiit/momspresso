package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 6/6/18.
 */

public class ShortStoryCommentListResponse extends BaseResponse {

    private List<ShortStoryCommentListData> data;
    private int count;

    public List<ShortStoryCommentListData> getData() {
        return data;
    }

    public void setData(List<ShortStoryCommentListData> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
