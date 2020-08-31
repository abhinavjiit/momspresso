package com.mycity4kids.ui.livestreaming

data class LiveStreamData(
    val attendees: List<Attendee>,
    val brand: Brand,
    val description: String,
    val event_type: Int,
    val id: Int,
    val image_url: String,
    val item_id: String,
    val item_meta: ItemMeta,
    val item_type: Int,
    val live_datetime: String,
    val location: Location,
    val name: String,
    val status: Int,
    val video_url: String
)
