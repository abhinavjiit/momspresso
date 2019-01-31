package com.mycity4kids.models.response

data class BaseResponseGeneric<T>(
        var code: Int = 0,
        var status: String? = null,
        var reason: String? = null,
        var data: DataGeneric<T>? = null
)