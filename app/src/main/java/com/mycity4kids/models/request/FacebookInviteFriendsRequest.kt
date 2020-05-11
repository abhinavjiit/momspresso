package com.mycity4kids.models.request

import com.google.gson.annotations.SerializedName

data class FacebookInviteFriendsRequest(
    @SerializedName("notifiedUsers")
    var notifiedUsers: ArrayList<String>
)
