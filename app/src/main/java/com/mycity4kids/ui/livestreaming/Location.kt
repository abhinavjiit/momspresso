package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    @SerializedName("address")
    val address: String? = "",
    @SerializedName("name")
    val name: String? = ""
) : Parcelable
