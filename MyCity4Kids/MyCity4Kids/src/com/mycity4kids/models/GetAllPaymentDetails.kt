package com.mycity4kids.models

import android.os.Parcel
import android.os.Parcelable

data class GetAllPaymentDetails(

        var account_address: String? = null,
        var account_ifsc_code: String? = null,
        var account_name: String? = null,
        var account_number: String? = null,
        var bank_name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_address)
        parcel.writeString(account_ifsc_code)
        parcel.writeString(account_name)
        parcel.writeString(account_number)
        parcel.writeString(bank_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetAllPaymentDetails> {
        override fun createFromParcel(parcel: Parcel): GetAllPaymentDetails {
            return GetAllPaymentDetails(parcel)
        }

        override fun newArray(size: Int): Array<GetAllPaymentDetails?> {
            return arrayOfNulls(size)
        }
    }
}
