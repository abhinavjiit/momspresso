package com.mycity4kids.ui.campaign

data class DefaultData(val account_address: String? = "",
                       val account_ifsc_code: String? = "",
                       val account_name: String? = "",
                       val account_number: String? = "",
                       val account_type: PaymentModesModal? = null,
                       val account_type_id: Int = 0,
                       val id: Int = 0,
                       val is_default: String? = "",
                       val is_last_used: String? = ""
) /*: Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(PaymentModesModal.class.getClassLoader()),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_address)
        parcel.writeString(account_ifsc_code)
        parcel.writeString(account_name)
        parcel.writeString(account_number)
        parcel.writeInt(account_type_id)
        parcel.writeInt(id)
        parcel.writeString(is_default)
        parcel.writeString(is_last_used)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefaultData> {
        override fun createFromParcel(parcel: Parcel): DefaultData {
            return DefaultData(parcel)
        }

        override fun newArray(size: Int): Array<DefaultData?> {
            return arrayOfNulls(size)
        }
    }
}
*/