package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.SerializedName

class ProofPostModel(
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("campaign_id")
        var campaign_id: Int? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("pan")
        val pan: String? = null,
        @SerializedName("url_type")
        val url_type : Int = -1


        /*sample values for url type*/
//0 : image link
//1: website url
//2: video url

)