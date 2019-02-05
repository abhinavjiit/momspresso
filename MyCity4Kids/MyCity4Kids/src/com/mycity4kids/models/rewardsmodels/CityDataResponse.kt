package com.mycity4kids.models.rewardsmodels

import android.os.Parcel
import android.os.Parcelable

data class CityDataResponse(
        var cityName : String? = null,
        var lon : String? = null,
        var id : String? = null,
        var lat : String? = null,
        var isSelected : Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            isSelected = parcel.readByte().toInt() != 0 ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cityName)
        parcel.writeString(lon)
        parcel.writeString(id)
        parcel.writeString(lat)
        parcel.writeByte((if (isSelected) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CityDataResponse> {
        override fun createFromParcel(parcel: Parcel): CityDataResponse {
            return CityDataResponse(parcel)
        }

        override fun newArray(size: Int): Array<CityDataResponse?> {
            return arrayOfNulls(size)
        }
    }
}