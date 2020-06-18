package com.mycity4kids.ui.campaign.fragment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CampaignFeedBack(
    @SerializedName("feedback")
    @Expose
    val feedback: ArrayList<String>
)
