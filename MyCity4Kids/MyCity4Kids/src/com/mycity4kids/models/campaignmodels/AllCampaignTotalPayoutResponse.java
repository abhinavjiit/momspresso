package com.mycity4kids.models.campaignmodels;

import com.mycity4kids.models.response.BaseResponse;

import java.util.List;


public class AllCampaignTotalPayoutResponse extends BaseResponse {
    private List<TotalPayoutData> data;

    public List<TotalPayoutData> getData() {
        return data;
    }

    public void setData(List<TotalPayoutData> data) {
        this.data = data;
    }

    public class TotalPayoutData {

        private String msg;
        private List<TotalPayoutResult> result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<TotalPayoutResult> getResult() {
            return result;
        }

        public void setResult(List<TotalPayoutResult> result) {
            this.result = result;
        }
    }

    public class TotalPayoutResult {

        private CampaignDetail campaign_details;
        private int final_payout;
        private String payment_meta;
        private int payment_status;


        public CampaignDetail getCampaignDetails() {
            return campaign_details;
        }

        public void setCampaignDetails(CampaignDetail campaignDetails) {
            this.campaign_details = campaignDetails;
        }

        public int getFinal_payout() {
            return final_payout;
        }

        public void setFinal_payout(int final_payout) {
            this.final_payout = final_payout;
        }

        public String getPayment_meta() {
            return payment_meta;
        }

        public void setPayment_meta(String payment_meta) {
            this.payment_meta = payment_meta;
        }

        public int getPayment_status() {
            return payment_status;
        }

        public void setPayment_status(int payment_status) {
            this.payment_status = payment_status;
        }

    }

    public class CampaignDetail {
        private BrandDetails brand_details;
        private String name;

        public BrandDetails getBrandDetails() {
            return brand_details;
        }

        public void setBrandDetails(BrandDetails brandDetails) {
            this.brand_details = brandDetails;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
