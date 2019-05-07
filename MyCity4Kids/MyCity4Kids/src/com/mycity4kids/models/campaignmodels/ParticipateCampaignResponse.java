package com.mycity4kids.models.campaignmodels;

import com.mycity4kids.models.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;


public class ParticipateCampaignResponse extends BaseResponse {
    private List<ParticipateData> data;

    public List<ParticipateData> getData() {
        return data;
    }

    public void setData(List<ParticipateData> data) {
        this.data = data;
    }

    public class ParticipateData {

        private String msg;
        private List<ParticipateResult> result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<ParticipateResult> getResult() {
            return result;
        }

        public void setResult(List<ParticipateResult> result) {
            this.result = result;
        }
    }

    public class ParticipateResult {

        private String id;

        public String getMsg() {
            return id;
        }

        public void setMsg(String id) {
            this.id = id;
        }
    }
}
