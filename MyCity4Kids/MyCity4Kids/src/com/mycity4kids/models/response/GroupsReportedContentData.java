package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 18/7/18.
 */

public class GroupsReportedContentData {

    private ArrayList<GroupReportedContentResult> result;

    public ArrayList<GroupReportedContentResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupReportedContentResult> result) {
        this.result = result;
    }
}
