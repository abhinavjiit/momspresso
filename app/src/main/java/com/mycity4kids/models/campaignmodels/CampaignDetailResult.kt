package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignDetailResult {

    @SerializedName("amount")
    @Expose
    var amount: Double? = null
    @SerializedName("approval_status")
    @Expose
    var approvalStatus: Int? = null
    @SerializedName("brand_details")
    @Expose
    var brandDetails: CampaignDetailBrandDetails? = null
    @SerializedName("brand_id")
    @Expose
    var brandId: Int? = null
    @SerializedName("campaign_status")
    @Expose
    var campaignStatus: Int? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: Int? = null
    @SerializedName("deliverable_types")
    @Expose
    var deliverableTypes: List<Int>? = null
    @SerializedName("deliverables")
    @Expose
    var deliverables: List<List<CampaignDetailDeliverable>>? = null
    @SerializedName("description")
    @Expose
    var description: CampaignDetailDescription? = null
    @SerializedName("end_time")
    @Expose
    var endTime: Long? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null
    @SerializedName("incentive_type")
    @Expose
    var incentiveType: Int? = null
    @SerializedName("is_active")
    @Expose
    var isActive: Int? = null
    @SerializedName("is_fixed_amount")
    @Expose
    var isFixedAmount: Int = 0
    @SerializedName("show_reffer_field")
    @Expose
    var showRefferField: Boolean? = false
    @SerializedName("max_amount")
    @Expose
    var maxAmount: Double? = null
    @SerializedName("max_slots")
    @Expose
    var maxSlots: Int? = null
    @SerializedName("min_amount")
    @Expose
    var minAmount: Double? = null
    @SerializedName("moderation_required")
    @Expose
    var moderationRequired: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("name_slug")
    @Expose
    var nameSlug: String? = null
    @SerializedName("po_number")
    @Expose
    var poNumber: String? = null
    @SerializedName("read_this")
    @Expose
    var readThis: CampaignDetailReadThis? = null
    @SerializedName("referral_code")
    @Expose
    var referralCode: String? = null
    @SerializedName("start_time")
    @Expose
    var startTime: Long? = null
    @SerializedName("terms")
    @Expose
    var terms: CampaignDetailTerms? = null
    @SerializedName("total_payout")
    @Expose
    var totalPayout: Double? = null
    @SerializedName("total_used_slots")
    @Expose
    var totalUsedSlots: Int? = null
    @SerializedName("voucher_brand")
    @Expose
    var voucherBrand: Int? = null

    /*@SerializedName("result")
    @Expose
    private val additionalProperties = HashMap<String, Any>()

    fun getAdditionalProperties(): Map<String, Any> {
        return this.additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        this.additionalProperties[name] = value
    }*/
}