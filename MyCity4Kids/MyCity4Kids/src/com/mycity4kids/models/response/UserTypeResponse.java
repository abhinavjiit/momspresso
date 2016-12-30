package com.mycity4kids.models.response;

import java.util.Map;

/**
 * Created by hemant on 29/12/16.
 */
public class UserTypeResponse extends BaseResponse {

    private UserTypeData data;

    public UserTypeData getData() {
        return data;
    }

    public void setData(UserTypeData data) {
        this.data = data;
    }

    public class UserTypeData {
        public Map<String, String> result;

        public Map<String, String> getResult() {
            return result;
        }

        public void setResult(Map<String, String> result) {
            this.result = result;
        }
    }
}
