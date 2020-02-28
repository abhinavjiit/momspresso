package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCampaignSubmissionDetailsResponse(
    @SerializedName("campaign_id")
    @Expose
    var campaignId: Int? = 0,
    @SerializedName("created_time")
    @Expose
    var createdTime: Int? = 0,
    @SerializedName("proofs")
    @Expose
    var campaignProofResponse: ArrayList<CampaignProofResponse>? = null,
    @SerializedName("updated_time")
    @Expose
    var updatedTime: Long? = 0,
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
)