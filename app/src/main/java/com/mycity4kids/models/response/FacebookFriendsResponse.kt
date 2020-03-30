package com.mycity4kids.models.response

import com.google.gson.annotations.SerializedName

data class FacebookFriendsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<FacebookFriendsData>?,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
)

data class FacebookFriendsData(
    @SerializedName("blogTitleSlug")
    val blogTitleSlug: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("profilePicUrl")
    val profilePicUrl: ProfilePic?,
    @SerializedName("userType")
    val userType: String?,
    @SerializedName("isFollowing")
    var isFollowing: String?
)
