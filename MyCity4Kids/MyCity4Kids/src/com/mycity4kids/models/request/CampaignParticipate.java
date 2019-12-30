package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

public class CampaignParticipate {
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("campaign_id")
    private int campaign_id;
    @SerializedName("payment_mode_id")
    private int payment_mode_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }

    public int getPayment_mode_id() {
        return payment_mode_id;
    }

    public void setPayment_mode_id(int payment_mode_id) {
        this.payment_mode_id = payment_mode_id;
    }

}
