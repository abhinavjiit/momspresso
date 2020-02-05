package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignDetailBrandDetails {
    @SerializedName("category_id")
    @Expose
    var categoryId: Any? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: String? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: Int? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null
    @SerializedName("is_active")
    @Expose
    var isActive: Int? = null
    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("updated_time")
    @Expose
    var updatedTime: Int? = null
}