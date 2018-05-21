package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 12/4/18.
 */

public class UserPostSettingData extends BaseData {

    private List<UserPostSettingResult> result;

    public List<UserPostSettingResult> getResult() {
        return result;
    }

    public void setResult(List<UserPostSettingResult> result) {
        this.result = result;
    }

}
