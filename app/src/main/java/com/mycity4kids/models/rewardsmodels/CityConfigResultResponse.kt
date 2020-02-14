package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.response.CityInfoItem

data class CityConfigResultResponse(
        @SerializedName("msg")
        @Expose
        var msg: String? = null,
        @SerializedName("cityData")
        @Expose
        var cityData: ArrayList<CityInfoItem>
)


