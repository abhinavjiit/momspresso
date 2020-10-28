package com.mycity4kids.models.response


import com.google.gson.annotations.SerializedName

data class SuggestedTopics(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var `data`: SuggestedTopicData,
    @SerializedName("error")
    var error: Boolean,
    @SerializedName("error_code")
    var errorCode: Any,
    @SerializedName("reason")
    var reason: String,
    @SerializedName("status")
    var status: String
)

data class SuggestedTopicData(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("result")
    var result: SuggestedTopicResult
)

data class SuggestedTopicResult(
    @SerializedName("suggestion")
    var suggestion: List<Suggestion>
)

data class Suggestion(
    @SerializedName("category_id")
    var categoryId: String,
    @SerializedName("display_name")
    var displayName: String,
    @SerializedName("itemType")
    var itemType: List<String>,
    var isFollowing: String ="0"
)

