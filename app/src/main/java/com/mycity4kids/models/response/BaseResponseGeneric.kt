package com.mycity4kids.models.response

import com.google.gson.annotations.SerializedName

data class BaseResponseGeneric<T>(
    @SerializedName("code")
    var code: Int = 0,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("reason")
    var reason: String? = null,
    @SerializedName("data")
    var data: DataGeneric<T>? = null
)