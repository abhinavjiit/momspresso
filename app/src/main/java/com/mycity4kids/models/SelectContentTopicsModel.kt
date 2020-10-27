package com.mycity4kids.models

import com.mycity4kids.models.response.BaseResponse

data class SelectContentTopicsModel(
    var data: ArrayList<Topics>?
) : BaseResponse() {
    var topics: ArrayList<SelectContentTopicsSubModel>? = null
}


