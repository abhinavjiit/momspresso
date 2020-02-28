package com.mycity4kids.models.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ShortStoryConfigData {

    @SerializedName("code")
    @Expose
    var code: Int = 0
    @SerializedName("data")
    @Expose
    var data: ShortStoryConfigDataResponse? = null
    @SerializedName("reason")
    @Expose
    var reason: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
}

class ShortStoryConfigDataResponse {
    @SerializedName("result")
    @Expose
    var result: ShortShortiesConfig? = null
}
