package com.mycity4kids.models.response

data class FacebookFriendsResponse(
    val code: Int,
    val `data`: List<FacebookFriendsData>?,
    val reason: String,
    val status: String
)

data class FacebookFriendsData(
    val blogTitleSlug: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val profilePicUrl: ProfilePic?,
    val userType: String?,
    var followStatus: String? = "0"
)
