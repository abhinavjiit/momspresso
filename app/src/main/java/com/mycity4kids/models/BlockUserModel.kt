package com.mycity4kids.models

class BlockUserModel(
    var blocked_user_id: String? = null,
    var blocking_area: IntArray = intArrayOf(1),
    var reason: String = ""
)
