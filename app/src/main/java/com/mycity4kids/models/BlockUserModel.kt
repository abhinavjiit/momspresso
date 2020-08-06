package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

class BlockUserModel(
    @SerializedName("blocked_user_id")
    var blocked_user_id: String? = null,
    @SerializedName("blocking_area")
    var blocking_area: IntArray = intArrayOf(1),
    @SerializedName("reason")
    var reason: String = ""
)
