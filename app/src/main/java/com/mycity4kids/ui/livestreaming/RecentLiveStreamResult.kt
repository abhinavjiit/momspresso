package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentLiveStreamResult(
    @SerializedName("events", alternate = ["events_list"])
    var events: ArrayList<LiveStreamResult>,
    @SerializedName("event_timerange")
    val event_timerange: Int
) : Parcelable
