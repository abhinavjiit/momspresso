package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;

public class CampaignReferral {
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("campaign_id")
    private int campaign_id;
    @SerializedName("referral_code")
    private String referral_code;

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

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

}
