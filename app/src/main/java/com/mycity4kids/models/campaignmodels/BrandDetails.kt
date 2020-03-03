package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BrandDetails {

    @SerializedName("category_id")
    @Expose
    var categoryId: Any? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: String? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: Int = 0
    @SerializedName("id")
    @Expose
    var id: Int = 0
    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null
    @SerializedName("is_active")
    @Expose
    var isActive: Int = 0
    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Int = 0
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("updated_time")
    @Expose
    var updatedTime: Int = 0
}
