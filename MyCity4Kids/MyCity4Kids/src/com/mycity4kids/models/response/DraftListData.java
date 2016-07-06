package com.mycity4kids.models.response;

import com.mycity4kids.models.editor.ArticleDraftList;

import java.util.ArrayList;

/**
 * Created by anshul on 7/5/16.
 */
public class DraftListData extends BaseData {
    private ArrayList<ArticleDraftList> result;

    public ArrayList<ArticleDraftList> getResult() {
        return result;
    }

    public void setResult(ArrayList<ArticleDraftList> data) {
        this.result = data;
    }
}
