package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 5/7/16.
 */
public class UpdateVideoDetailsResponse extends BaseResponse {

    private UpdateVideoDetailsData data;

    public UpdateVideoDetailsData getData() {
        return data;
    }

    public void setData(UpdateVideoDetailsData data) {
        this.data = data;
    }

    public class UpdateVideoDetailsData {

        private String msg;
        private String result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

}
