package com.mycity4kids.models.response;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchResponse extends BaseResponse {

    private SearchData data;

    public SearchData getData() {
        return data;
    }

    public void setData(SearchData data) {
        this.data = data;
    }
}
