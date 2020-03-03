package com.mycity4kids.profile

import com.google.gson.annotations.SerializedName

data class MilestonesResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: MilestonesData?,
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("error_code")
    val error_code: Any?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("status")
    val status: String?
)

data class MilestonesData(
    @SerializedName("result")
    val result: List<MilestonesResult>?
)

data class MilestonesResult(
    @SerializedName("content_id")
    val content_id: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("item_type")
    val item_type: String?,
    @SerializedName("meta_image")
    val meta_image: String?,
    @SerializedName("milestone_bg_url")
    val milestone_bg_url: String?,
    @SerializedName("milestone_desc")
    val milestone_desc: String?,
    @SerializedName("milestone_image_url")
    val milestone_image_url: String?,
    @SerializedName("milestone_metaclass")
    val milestone_metaclass: String?,
    @SerializedName("milestone_name")
    val milestone_name: String?,
    @SerializedName("milestone_sharing_url")
    val milestone_sharing_url: String?,
    @SerializedName("milestone_title")
    val milestone_title: String?,
    @SerializedName("user_id")
    val user_id: String?,
    @SerializedName("meta_data")
    val meta_data: MilestoneMetaData?
)

data class MilestoneMetaData(
    @SerializedName("content_info")
    val content_info: ContentInfo?
)

data class ContentInfo(
    @SerializedName("imageUrl")
    val imageUrl: Any?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("created_at")
    val created_at: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("payment_value")
    val payment_value: Long?,
    @SerializedName("referral_code")
    val referral_code: String?
)
