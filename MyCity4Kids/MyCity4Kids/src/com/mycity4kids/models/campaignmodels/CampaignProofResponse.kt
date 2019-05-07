package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignProofResponse(
        @SerializedName("comments")
        @Expose
        var comments: Any? = null,
        @SerializedName("created_time")
        @Expose
        var createdTime: Int? = 0,
        @SerializedName("id")
        @Expose
        var id: Int? = 0,
        @SerializedName("is_deleted")
        @Expose
        var isDeleted: Int? = 0,
        @SerializedName("proof_status")
        @Expose
        var proofStatus: Int? = 0,  //refer from doc for the values
        @SerializedName("submission_id")
        @Expose
        var submissionId: Int? = 0,
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null,
        @SerializedName("updated_time")
        @Expose
        var updatedTime: Int? = 0,
        @SerializedName("url")
        @Expose
        var url: String? = null,
        @SerializedName("url_type")
        @Expose
        var urlType: Int? = null,

        var isEditable : Boolean? = false
)