package com.mycity4kids.models.campaignmodels

import android.os.Parcel
import android.os.Parcelable

class CampaignDesc() : Parcelable {
    var text: String? = null

    constructor(parcel: Parcel) : this() {
        text = parcel.readString()
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CampaignDesc> {
        override fun createFromParcel(parcel: Parcel): CampaignDesc {
            return CampaignDesc(parcel)
        }

        override fun newArray(size: Int): Array<CampaignDesc?> {
            return arrayOfNulls(size)
        }
    }
}