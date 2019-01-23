package com.mycity4kids.models.response

data class DataGeneric<T>(
        var msg: String,
        var result: T
)