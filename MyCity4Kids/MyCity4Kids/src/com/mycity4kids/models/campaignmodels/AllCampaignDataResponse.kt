package com.mycity4kids.models.campaignmodels

import android.os.Parcel
import android.os.Parcelable

data class AllCampaignDataResponse(

        var amount: String? = null,
        var approval_status: String? = null,
        var approved_by: String? = null,
        var approved_time: String? = null,
        var brand_id: String? = null,
        var created_by: String? = null,
        var created_time: String? = null,
        var deliverable_types: String? = null,
        var start_time : String? = null,
        var end_time: String? = null,
        var id: String? = null,
        var image_url: String? = null,
        var incentive_type: String? = null,
        var is_active: String? = null,
        var is_deleted: String? = null,
        var is_fixed_amount: String? = null,
        var max_amount: String? = null,
        var max_slots: String? = null,
        var min_amount: String? = null,
        var moderation_required: String? = null,
        var name: String? = null,
        var po_number: String? = null,

        var total_payout: String? = null,
        var updated_time: String? = null,
        var voucher_brand: String? = null,
        var description : CampaignDesc? = null

        /*var"description": {
    "text": "some description about campaign "
},
var"read_this": {
    "instructions": [
    "lorem ipsum ",
    "ipsum chipsum",
    "chipsum lorem"
    ]
},
var start_time : String? = null,
var"terms": {
    "instructions": [
    "lorem ipsum ",
    "ipsum chipsum",
    "chipsum lorem"
    ]
},*/

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable<CampaignDesc>(CampaignDesc::class.java.classLoader)) {
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(amount)
        p0.writeString(approval_status)
        p0.writeString(approved_by)
        p0.writeString(approved_time)
        p0.writeString(brand_id)
        p0.writeString(created_by)
        p0.writeString(created_time)
        p0.writeString(deliverable_types)
        p0.writeString(start_time)
        p0.writeString(end_time)
        p0.writeString(id)
        p0.writeString(image_url)
        p0.writeString(incentive_type)
        p0.writeString(is_active)
        p0.writeString(is_deleted)
        p0.writeString(is_fixed_amount)
        p0.writeString(max_amount)
        p0.writeString(max_slots)
        p0.writeString(min_amount)
        p0.writeString(moderation_required)
        p0.writeString(name)
        p0.writeString(po_number)
        p0.writeString(total_payout)
        p0.writeString(updated_time)
        p0.writeString(voucher_brand)
        p0.writeParcelable(description,p1)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllCampaignDataResponse> {
        override fun createFromParcel(parcel: Parcel): AllCampaignDataResponse {
            return AllCampaignDataResponse(parcel)
        }

        override fun newArray(size: Int): Array<AllCampaignDataResponse?> {
            return arrayOfNulls(size)
        }
    }
}