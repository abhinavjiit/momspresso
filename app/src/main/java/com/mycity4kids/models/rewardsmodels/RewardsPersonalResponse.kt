package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RewardsPersonalResponse {

    @SerializedName("code")
    @Expose
    var code: Int = 0
    @SerializedName("data")
    @Expose
    var data: Any? = null
    @SerializedName("reason")
    @Expose
    var reason: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
}
