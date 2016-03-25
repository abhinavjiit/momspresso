package com.mycity4kids.models.editor;

import com.mycity4kids.models.BaseResponseModel;
import com.mycity4kids.models.parentingdetails.ParentingDetailsResult;

/**
 * Created by anshul on 3/14/16.
 */
public class ArticleDraftsModel extends BaseResponseModel {

    private ParentingDetailsResult result;

    public ParentingDetailsResult getResult() {
        return result;
    }

    public void setResult(ParentingDetailsResult result) {
        this.result = result;
    }

}
