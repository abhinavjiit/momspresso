package com.mycity4kids.models.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.profile.Crown

class CrownData {
    @SerializedName("result")
    @Expose
    var result: Crown? = null
}
