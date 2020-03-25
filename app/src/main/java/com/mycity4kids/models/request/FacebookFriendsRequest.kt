package com.mycity4kids.models.request

import com.google.gson.annotations.SerializedName

data class FacebookFriendsRequest(
    @SerializedName("userAccessToken")
    val userAccessToken: String,
    @SerializedName("userFbSocialId")
    val userFbSocialId: String
)
