package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

class NotificationEnabledOrDisabledModel(
    @SerializedName("SubscriptionTypeID")
    var id: Int,
    @SerializedName("Enabled")
    var enabled: Boolean
)
