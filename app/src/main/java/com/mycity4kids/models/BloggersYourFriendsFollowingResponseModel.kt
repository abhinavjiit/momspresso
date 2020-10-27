package com.mycity4kids.models

import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.response.BaseResponse
import com.mycity4kids.models.response.FacebookInviteFriendsData

data class BloggersYourFriendsFollowingResponseModel(
    @SerializedName("data")
    var data: BloggersData
) : BaseResponse()

data class BloggersData(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("result")
    var result: BloggersResult?
)

data class BloggersResult(
    @SerializedName("suggestion")
    var suggestion: List<FacebookInviteFriendsData>?

)




