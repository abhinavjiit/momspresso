package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by anshul on 7/5/16.
 */
public class DraftListData extends BaseData {
    private ArrayList<PublishDraftObject> result;

    public ArrayList<PublishDraftObject> getResult() {
        return result;
    }

    public void setResult(ArrayList<PublishDraftObject> data) {
        this.result = data;
    }
}
