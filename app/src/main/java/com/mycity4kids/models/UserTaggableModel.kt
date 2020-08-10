package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

data class UserTaggableModel(
    @SerializedName(
        "isTaggable"
    )
    var isTaggable: String? = null
)
