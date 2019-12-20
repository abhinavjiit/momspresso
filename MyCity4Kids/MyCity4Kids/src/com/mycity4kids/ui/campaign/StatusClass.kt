package com.mycity4kids.ui.campaign

import com.google.gson.annotations.SerializedName

data class StatusClass(
        @SerializedName("recm_status")
        var recm_status: Int = 0
)