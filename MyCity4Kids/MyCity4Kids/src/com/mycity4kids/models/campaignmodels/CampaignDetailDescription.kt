package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignDetailDescription {
    @SerializedName("instructions")
    @Expose
    var instructions: List<String>? = null
}