package com.mycity4kids.models.rewardsmodels

import com.mycity4kids.models.city.CityData
import com.mycity4kids.models.response.CityInfoItem

data class CityConfigResultResponse (
        var msg : String? = null,
        //var result : CityResultDataResponse
        var cityData : ArrayList<CityInfoItem>
)


