package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 15/11/16.
 */
public class RecommendUnrecommendArticleResponse extends BaseResponse {
    private List<String> data = new ArrayList<String>();

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
