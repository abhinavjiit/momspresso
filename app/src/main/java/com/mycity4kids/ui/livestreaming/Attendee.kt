package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attendee(
    @SerializedName("about")
    val about: String? = "",
    @SerializedName("attendee_type")
    val attendee_type: AttendeeType? = null,
    @SerializedName("company_name")
    val company_name: String? = null,
    @SerializedName("designation")
    val designation: String? = "",
    @SerializedName("first_name")
    val first_name: String? = "",
    @SerializedName("image_url")
    val image_url: String? = "",
    @SerializedName("last_name")
    val last_name: String? = "",
    @SerializedName("user_id")
    val user_id: String? = ""
) : Parcelable
