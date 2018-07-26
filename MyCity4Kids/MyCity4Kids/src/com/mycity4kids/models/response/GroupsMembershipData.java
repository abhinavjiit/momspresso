package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 19/7/18.
 */

public class GroupsMembershipData {
    private List<GroupsMembershipResult> result;

    public List<GroupsMembershipResult> getResult() {
        return result;
    }

    public void setResult(List<GroupsMembershipResult> result) {
        this.result = result;
    }
}
