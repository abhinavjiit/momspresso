package com.mycity4kids.ui.livestreaming

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveStreamResult(
    @SerializedName("attendees")
    val attendees: List<Attendee>? = ArrayList(),
    @SerializedName("brand")
    val brand: Brand? = null,
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("event_type")
    val event_type: Int? = -1,
    @SerializedName("id")
    val id: Int? = -1,
    @SerializedName("image_url")
    val image_url: String? = "",
    @SerializedName("cover_image")
    val cover_image: String? = "",
    @SerializedName("item_id")
    val item_id: String? = "",
    @SerializedName("item_meta")
    val item_meta: ItemMeta? = null,
    @SerializedName("item_type")
    val item_type: Int? = -1,
    @SerializedName("live_datetime")
    val live_datetime: Long? = 0,
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("status")
    val status: Int? = -1,
    @SerializedName("video_url")
    val video_url: String? = "",
    @SerializedName("updated_at")
    val updated_at: Long? = 0,
    @SerializedName("slug")
    val slug: String? = "",
    val liveStatus: Int = 0
) : Parcelable
