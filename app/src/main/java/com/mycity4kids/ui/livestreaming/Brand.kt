package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Brand(
    @SerializedName("brand_image")
    val brand_image: String?,
    @SerializedName("brand_name")
    val brand_name: String?,
    @SerializedName("id")
    val id: Int
) : Parcelable
