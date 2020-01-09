package com.mycity4kids.models.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ShortStoryImageData {

    @SerializedName("code")
    @Expose
    var code: Int = 0
    @SerializedName("data")
    @Expose
    var data: ShortStoryImageDataResponse? = null
    @SerializedName("reason")
    @Expose
    var reason: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null


}

class ShortStoryImageDataResponse {
    @SerializedName("result")
    @Expose
    var result: ShortShortiesBackgroundThumbnail? = null
}
