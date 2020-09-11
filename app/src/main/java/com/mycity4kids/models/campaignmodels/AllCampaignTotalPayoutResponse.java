package com.mycity4kids.models.campaignmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.BaseResponse;

import java.util.List;


public class AllCampaignTotalPayoutResponse extends BaseResponse {
    @SerializedName("data")
    private List<TotalPayoutData> data;

    public List<TotalPayoutData> getData() {
        return data;
    }

    public void setData(List<TotalPayoutData> data) {
        this.data = data;
    }

    public class TotalPayoutData {
        @SerializedName("msg")
        private String msg;
        @SerializedName("result")
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
        @SerializedName("campaign_details")
        private CampaignDetail campaign_details;
        @SerializedName("expected_payment_time")
        private Long expectedDate;
        @SerializedName("final_payout")
        private double final_payout;
        @SerializedName("gateway_update_time")
        private Long paidDate;
        @SerializedName("reimbursement")
        private double reimbursement;
        @SerializedName("payment_meta")
        private List<PaymentMeta> payment_meta;
        @SerializedName("payment_status")
        private int payment_status;

        public double getReimbursement() {
            return reimbursement;
        }

        public void setReimbursement(double reimbursement) {
            this.reimbursement = reimbursement;
        }

        public CampaignDetail getCampaignDetails() {
            return campaign_details;
        }

        public void setCampaignDetails(CampaignDetail campaignDetails) {
            this.campaign_details = campaignDetails;
        }

        public double getFinal_payout() {
            return final_payout + reimbursement;
        }


        public Long getExpectedDate() {
            return expectedDate;
        }

        public void setExpectedDate(Long expectedDate) {
            this.expectedDate = expectedDate;
        }

        public Long getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(Long paidDate) {
            this.paidDate = paidDate;
        }

        public void setFinal_payout(double final_payout) {
            this.final_payout = final_payout;
        }

        public List<PaymentMeta> getPayment_meta() {
            return payment_meta;
        }

        public void setPayment_meta(List<PaymentMeta> payment_meta) {
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
        @SerializedName("brand_details")
        private BrandDetails brand_details;
        @SerializedName("name")
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

    public class PaymentMeta {
        @SerializedName("account")
        private Account account;
        @SerializedName("id")
        private int id;
        @SerializedName("net_amount")
        private Double net_amount;
        @SerializedName("source")
        private String source;
        @SerializedName("status")
        private String status;
        @SerializedName("tax_amount")
        private Double tax_amount;
        @SerializedName("tax_percentage")
        private Double tax_percentage;
        @SerializedName("total_amount")
        private Double total_amount;
        @SerializedName("transaction")
        private Transaction transaction;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Double getNet_amount() {
            return net_amount;
        }

        public void setNet_amount(Double net_amount) {
            this.net_amount = net_amount;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Double getTax_amount() {
            return tax_amount;
        }

        public void setTax_amount(Double tax_amount) {
            this.tax_amount = tax_amount;
        }

        public Double getTax_percentage() {
            return tax_percentage;
        }

        public void setTax_percentage(Double tax_percentage) {
            this.tax_percentage = tax_percentage;
        }

        public Double getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(Double total_amount) {
            this.total_amount = total_amount;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    public class Account {
        @SerializedName("account_number")
        private String account_number;
        @SerializedName("id")
        private int id;

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public class Transaction {
        @SerializedName("code")
        private String code;
        @SerializedName("status")
        private String status;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}