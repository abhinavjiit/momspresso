package com.mycity4kids.models.response

import com.google.gson.annotations.SerializedName

data class FacebookInviteFriendsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: List<dataList>?,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
)

data class dataList(
    @SerializedName("friendList")
    val friendList: List<FacebookInviteFriendsData>?,
    @SerializedName("hasExpired")
    val hasExpired: Boolean = false
)

data class FacebookInviteFriendsData(
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
    var isFollowing: String?,
    @SerializedName("isInvited")
    var isInvited: String? = "0",
    @SerializedName("friendscount")
    var friendsCount: String,
    @SerializedName("friendsuserList")
    var userFriendsList:List<UserDetailResult>
)
