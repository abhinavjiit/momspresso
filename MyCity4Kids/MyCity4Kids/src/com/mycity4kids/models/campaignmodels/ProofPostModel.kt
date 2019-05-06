package com.mycity4kids.models.campaignmodels

class ProofPostModel(
        var url: String? = null,
        var campaign_id: Int? = null,
        var id: String? = null,
        val name: String? = null,
        val pan: String? = null,
        val url_type : Int = -1


        /*sample values for url type*/
//0 : image link
//1: website url
//2: video url

)