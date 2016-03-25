package com.mycity4kids.models.editor;

import com.mycity4kids.models.CommonMessage;

import java.util.ArrayList;

/**
 * Created by anshul on 3/16/16.
 */
public class ArticleDraftListResult extends CommonMessage {
    private ArrayList<ArticleDraftList> data;

    public ArrayList<ArticleDraftList> getData() {
        return data;
    }

    public void setData(ArrayList<ArticleDraftList> data) {
        this.data = data;
    }
}
