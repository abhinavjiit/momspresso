package com.mycity4kids.models.campaignmodels;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.BaseResponse;

import java.util.List;


public class TotalPayoutResponse extends BaseResponse {

    @SerializedName("data")
    private List<PayoutData> data;

    public List<PayoutData> getData() {
        return data;
    }

    public void setData(List<PayoutData> data) {
        this.data = data;
    }

    public class PayoutData {
        @SerializedName("msg")
        private String msg;
        @SerializedName("result")
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
        @SerializedName("total_payout")
        private double total_payout;
        @SerializedName("total_payout_campaign_count")
        private int total_payout_campaign_count;

        public double getTotal_payout() {
            return total_payout;
        }

        public void setTotal_payout(int total_payout) {
            this.total_payout = total_payout;
        }

        public int getTotal_payout_campaign_count() {
            return total_payout_campaign_count;
        }

        public void setTotal_payout_campaign_count(int total_payout_campaign_count) {
            this.total_payout_campaign_count = total_payout_campaign_count;
        }

    }
}
