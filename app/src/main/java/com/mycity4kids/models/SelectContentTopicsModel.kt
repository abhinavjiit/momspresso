package com.mycity4kids.models

import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.response.BaseResponse

data class SelectContentTopicsModel(
    @SerializedName("data")
    var data: SelectedContentTopicsResultResponse?
) : BaseResponse() {
    @SerializedName("topics")
    var topics: ArrayList<SelectContentTopicsSubModel>? = null
}


data class SelectedContentTopicsResultResponse(
    @SerializedName("result")
    var result: ArrayList<Topics>
)

