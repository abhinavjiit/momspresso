package com.mycity4kids.models.editor;

import com.mycity4kids.models.parentingstop.CommonParentingList;

import java.util.ArrayList;

/**
 * Created by anshul on 3/16/16.
 */
public class ArticleDraftData {
    private ArrayList<ArticleDraftList> draftList;

    public ArrayList<ArticleDraftList> getDraftList() {
        return draftList;
    }

    public void setDraftList(ArrayList<ArticleDraftList> draftList) {
        this.draftList = draftList;
    }
}
