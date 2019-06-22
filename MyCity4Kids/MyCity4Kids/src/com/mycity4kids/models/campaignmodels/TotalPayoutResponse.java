package com.mycity4kids.models.campaignmodels;

import com.mycity4kids.models.response.BaseResponse;

import java.util.List;


public class TotalPayoutResponse extends BaseResponse {

    private List<PayoutData> data;

    public List<PayoutData> getData() {
        return data;
    }

    public void setData(List<PayoutData> data) {
        this.data = data;
    }

    public class PayoutData {

        private String msg;
        private List<PayoutResult> result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<PayoutResult> getResult() {
            return result;
        }

        public void setResult(List<PayoutResult> result) {
            this.result = result;
        }
    }

    public class PayoutResult {
        private int total_payout;

        public int getTotal_payout() {
            return total_payout;
        }

        public void setTotal_payout(int total_payout) {
            this.total_payout = total_payout;
        }

    }
}
