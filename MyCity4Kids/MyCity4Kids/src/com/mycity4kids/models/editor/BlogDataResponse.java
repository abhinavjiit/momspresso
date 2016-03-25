package com.mycity4kids.models.editor;

import com.mycity4kids.models.BaseResponseModel;

/**
 * Created by anshul on 3/20/16.
 */
public class BlogDataResponse extends BaseResponseModel {
 private BlogDataResult result;

    public BlogDataResult getResult() {
        return result;
    }

    public void setResult(BlogDataResult result) {
        this.result = result;
    }
}
