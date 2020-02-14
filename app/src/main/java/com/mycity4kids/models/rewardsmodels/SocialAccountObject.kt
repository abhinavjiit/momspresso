package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SocialAccountObject(
        @SerializedName("platform_name")
        @Expose
        var platform_name: String? = null,
        @SerializedName("acc_link")
        @Expose
        var acc_link: String? = null,
        @SerializedName("id")
        @Expose
        var id: Int? = null,
        @SerializedName("access_token")
        @Expose
        var access_token: String? = null
)
