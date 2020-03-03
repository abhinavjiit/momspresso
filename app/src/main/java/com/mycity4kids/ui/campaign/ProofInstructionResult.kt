package com.mycity4kids.ui.campaign.fragment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.campaignmodels.CampaignDetailReadThis

data class ProofInstructionResult(
    @SerializedName("proof_instructions")
    @Expose
    var readThis: CampaignDetailReadThis? = null
)
