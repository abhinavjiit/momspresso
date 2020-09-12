package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveStreamData(
    @SerializedName("result")
    val result: LiveStreamResult? = null
) : Parcelable
