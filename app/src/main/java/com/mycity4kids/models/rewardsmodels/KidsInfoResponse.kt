package com.mycity4kids.models.rewardsmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KidsInfoResponse(
    @SerializedName("dob")
    @Expose
    var dob: Long? = null,
    @SerializedName("expected_date")
    @Expose
    var expected_date: Long? = null,
    @SerializedName("gender")
    @Expose
    var gender: Int? = null,
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null
)