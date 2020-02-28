package com.mycity4kids.ui.mymoneytracker.model

import com.google.gson.annotations.SerializedName

data class TrackerDataModel(
    @SerializedName("completed_time")
    var completed_time: Long,
    @SerializedName("expected_time")
    var expected_time: Long,
    @SerializedName("is_completed")
    var is_completed: Int,
    @SerializedName("tracker_status")
    var tracker_status: Int
)
