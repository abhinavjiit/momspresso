package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

data class BlockUserModel(
    @SerializedName("blocked_user_id")
    var blocked_user_id: String? = null,
    @SerializedName("blocking_area")
    var blocking_area: IntArray = intArrayOf(1),
    @SerializedName("reason")
    var reason: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockUserModel

        if (blocked_user_id != other.blocked_user_id) return false
        if (!blocking_area.contentEquals(other.blocking_area)) return false
        if (reason != other.reason) return false

        return true
    }

    override fun hashCode(): Int {
        var result = blocked_user_id?.hashCode() ?: 0
        result = 31 * result + blocking_area.contentHashCode()
        result = 31 * result + reason.hashCode()
        return result
    }
}

