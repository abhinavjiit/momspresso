package com.mycity4kids.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetAllPaymentDetails(
        @SerializedName("account_address")
        @Expose
        var account_address: String? = null,
        @SerializedName("account_ifsc_code")
        @Expose
        var account_ifsc_code: String? = null,
        @SerializedName("account_name")
        @Expose
        var account_name: String? = null,
        @SerializedName("account_number")
        @Expose
        var account_number: String? = null,
        @SerializedName("bank_name")
        @Expose
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
