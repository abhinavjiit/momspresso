package com.mycity4kids.ui.campaign

import com.google.gson.annotations.SerializedName

data class BasicResponse(
        @SerializedName("code")
        var code: Int = 0,
        @SerializedName("data")
        var data: DataClass,
        @SerializedName("reason")
        var reason: String? = null,
        @SerializedName("status")
        var status: String? = null
)
