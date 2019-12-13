package com.mycity4kids.models.collectionsModels

import com.mycity4kids.models.response.BaseResponse
import com.mycity4kids.models.response.MixFeedResult

class FeaturedOnModel : BaseResponse() {

    var data: FeaturedData? = null

    inner class FeaturedData {
        var result: FeaturedResult? = null
        var msg: String? = null
    }

    inner class FeaturedResult {
        var item_list: List<MixFeedResult>? = null
        var total_items: Int = 0
    }
}

