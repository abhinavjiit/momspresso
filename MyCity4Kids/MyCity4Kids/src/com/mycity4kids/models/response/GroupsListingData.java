package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupsListingData extends BaseData {

    private ArrayList<GroupResult> result;

    public ArrayList<GroupResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupResult> result) {
        this.result = result;
    }

}
