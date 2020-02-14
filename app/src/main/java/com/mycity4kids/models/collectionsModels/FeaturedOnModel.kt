package com.mycity4kids.models.collectionsModels

import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.response.BaseResponse
import com.mycity4kids.models.response.MixFeedResult

class FeaturedOnModel : BaseResponse() {

    @SerializedName("data")
    var data: FeaturedData? = null

    inner class FeaturedData {
        @SerializedName("result")
        var result: FeaturedResult? = null
        @SerializedName("msg")
        var msg: String? = null
    }

    inner class FeaturedResult {
        @SerializedName("item_list")
        var item_list: List<MixFeedResult>? = null
        @SerializedName("total_items")
        var total_items: Int = 0
    }
}

