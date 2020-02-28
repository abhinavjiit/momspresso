package com.mycity4kids.ui.campaign

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentModeListModal(
    @SerializedName("available")
    @Expose
    val available: List<PaymentModesModal>? = null,
    @SerializedName("default")
    @Expose
    val default: DefaultData? = null
)
