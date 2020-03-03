package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllCampaignDataResponse {

        @SerializedName("code")
        @Expose
        var code: Int = 0
        @SerializedName("data")
        @Expose
        var data: CampaignListData? = null
        @SerializedName("reason")
        @Expose
        var reason: String? = null
        @SerializedName("status")
        @Expose
        var status: String? = null
}
