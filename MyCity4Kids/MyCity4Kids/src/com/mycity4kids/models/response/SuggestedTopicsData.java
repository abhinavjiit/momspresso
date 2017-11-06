package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsData extends BaseData {

    private Map<String, ArrayList<String>> result;

    public Map<String, ArrayList<String>> getResult() {
        return result;
    }

    public void setResult(Map<String, ArrayList<String>> result) {
        this.result = result;
    }
}
