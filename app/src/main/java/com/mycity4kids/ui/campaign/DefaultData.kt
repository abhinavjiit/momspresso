package com.mycity4kids.ui.campaign

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DefaultData(
    @SerializedName("account_address")
    @Expose
    val account_address: String? = "",
    @SerializedName("account_ifsc_code")
    @Expose
    val account_ifsc_code: String? = "",
    @SerializedName("account_name")
    @Expose
    val account_name: String? = "",
    @SerializedName("account_number")
    @Expose
    val account_number: String? = "",
    @SerializedName("account_type")
    @Expose
    val account_type: PaymentModesModal? = null,
    @SerializedName("account_type_id")
    @Expose
    val account_type_id: Int = 0,
    @SerializedName("id")
    @Expose
    val id: Int = 0,
    @SerializedName("is_default")
    @Expose
    val is_default: String? = "",
    @SerializedName("is_last_used")
    @Expose
    val is_last_used: String? = ""
)
