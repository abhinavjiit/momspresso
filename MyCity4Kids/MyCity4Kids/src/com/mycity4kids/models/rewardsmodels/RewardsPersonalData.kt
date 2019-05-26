package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.campaignmodels.CampaignDataListResult

class RewardsPersonalData {
    @SerializedName("result")
    @Expose
    var result: ArrayList<CampaignDataListResult>? = null
}