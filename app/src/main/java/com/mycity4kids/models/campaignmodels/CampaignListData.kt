package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignListData {
    @SerializedName("result")
    @Expose
    var result: ArrayList<CampaignDataListResult>? = null
}