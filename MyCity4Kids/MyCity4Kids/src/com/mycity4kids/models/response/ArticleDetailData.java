package com.mycity4kids.models.response;

import com.mycity4kids.models.parentingdetails.DetailsBody;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 6/7/16.
 */
public class ArticleDetailData {
    private String msg;
    private ArticleDetailResult result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArticleDetailResult getResult() {
        return result;
    }

    public void setResult(ArticleDetailResult result) {
        this.result = result;
    }
}
