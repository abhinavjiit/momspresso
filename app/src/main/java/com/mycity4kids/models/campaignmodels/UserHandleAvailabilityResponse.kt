package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.SerializedName

data class UserHandleAvailabilityResponse(
    @SerializedName("userId")
    var userId: String = ""
)
