package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.HashMap

class CampaignDetailTerms {
    @SerializedName("instructions")
    @Expose
    var instructions: List<String>? = null

    /*private val additionalProperties = HashMap<String, Any>()

    fun getAdditionalProperties(): Map<String, Any> {
        return this.additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        this.additionalProperties[name] = value
    }*/

}