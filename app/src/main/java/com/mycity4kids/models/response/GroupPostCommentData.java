package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 21/5/18.
 */

public class GroupPostCommentData {
    @SerializedName("result")
    private List<GroupPostCommentResult> result;

    public List<GroupPostCommentResult> getResult() {
        return result;
    }

    public void setResult(List<GroupPostCommentResult> result) {
        this.result = result;
    }

}
