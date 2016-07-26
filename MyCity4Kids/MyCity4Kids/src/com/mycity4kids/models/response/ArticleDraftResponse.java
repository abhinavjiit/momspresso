package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/4/16.
 */
public class ArticleDraftResponse extends BaseResponse {

    ArticleDraftData data;

    public ArticleDraftData getData() {
        return data;
    }

    public void setData(ArticleDraftData data) {
        this.data = data;
    }
}
