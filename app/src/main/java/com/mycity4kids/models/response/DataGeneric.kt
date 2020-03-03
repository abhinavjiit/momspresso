package com.mycity4kids.models.response

import com.google.gson.annotations.SerializedName

data class DataGeneric<T>(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("result")
    var result: T
)
