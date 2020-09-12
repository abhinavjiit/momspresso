package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentLiveStreamData(
    @SerializedName("result")
    val result: RecentLiveStreamResult
) : Parcelable
