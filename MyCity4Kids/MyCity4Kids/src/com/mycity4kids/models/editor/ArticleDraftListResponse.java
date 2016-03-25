package com.mycity4kids.models.editor;

import com.mycity4kids.models.BaseResponseModel;

/**
 * Created by anshul on 3/16/16.
 */
public class ArticleDraftListResponse extends BaseResponseModel {
    private ArticleDraftListResult result;

    public ArticleDraftListResult getResult() {
        return result;
    }

    public void setResult(ArticleDraftListResult result) {
        this.result = result;
    }
}
