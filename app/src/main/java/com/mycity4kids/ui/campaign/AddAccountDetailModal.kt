package com.mycity4kids.ui.campaign

import com.google.gson.annotations.SerializedName

data class AddAccountDetailModal(
        @SerializedName("account_type_id")
        val account_type_id: String? = null,
        @SerializedName("account_number")
        val account_number: String? = null,
        @SerializedName("account_name")
        val account_name: String? = null,
        @SerializedName("account_ifsc_code")
        val account_ifsc_code: String? = null
)
