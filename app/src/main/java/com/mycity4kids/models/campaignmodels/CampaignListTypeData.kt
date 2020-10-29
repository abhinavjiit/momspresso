package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mycity4kids.models.response.BaseResponse

class CampaignListTypeData : BaseResponse() {
    @SerializedName("msg")
    private val msg: String? = null
    @SerializedName("result")
    @Expose
    var result: CampaignListTypeResult? = null
}

class CampaignListTypeResult {
    @SerializedName("deliverable_types")
    @Expose
    var deliverable_types: ArrayList<DeliverableType>? = null
}

data class DeliverableType(
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("display_name")
    @Expose
    var display_name: String? = null,
    @SerializedName("tax_header")
    @Expose
    var tax_header: String? = null,
    @SerializedName("url_type")
    @Expose
    var url_type: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null
)