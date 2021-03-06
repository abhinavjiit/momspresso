package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveStreamResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: LiveStreamData,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: String?,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
) : Parcelable
