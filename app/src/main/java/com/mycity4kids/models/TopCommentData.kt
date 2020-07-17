package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

data class TopCommentData(
    @SerializedName("post_id")
    var postId: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("status")
    var status: Boolean
)
