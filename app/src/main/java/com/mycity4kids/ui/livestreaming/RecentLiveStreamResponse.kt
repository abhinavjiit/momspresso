package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentLiveStreamResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: RecentLiveStreamData,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: String?,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
) : Parcelable
