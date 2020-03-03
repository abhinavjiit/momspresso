package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.SerializedName

data class ReferralCodeResult(
    @SerializedName("referral_code")
    var referral_code: String = "",
    @SerializedName("is_valid")
    var is_valid: Boolean = false
)
