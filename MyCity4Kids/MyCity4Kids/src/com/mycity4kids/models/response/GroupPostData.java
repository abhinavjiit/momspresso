package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 12/4/18.
 */

public class GroupPostData extends BaseData {

    private ArrayList<GroupPostResult> result;

    public ArrayList<GroupPostResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupPostResult> result) {
        this.result = result;
    }

}
