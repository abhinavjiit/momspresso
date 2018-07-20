package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/18.
 */

public class GroupsMembershipData {
    private ArrayList<GroupsMembershipResult> result;

    public ArrayList<GroupsMembershipResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupsMembershipResult> result) {
        this.result = result;
    }
}
