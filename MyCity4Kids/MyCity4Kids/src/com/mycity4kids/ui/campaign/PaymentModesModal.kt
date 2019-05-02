package com.mycity4kids.ui.campaign

import android.os.Parcel
import android.os.Parcelable

data class PaymentModesModal(
        var type_id: Int = 0,
        var icon: String? = null,
        var name: String? = null,
        var isChecked: Boolean = false,
        var isDefault: Boolean = false,
        var accountNumber: String? = null,
        var id: Int = 0,
        val exists: Exists? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readParcelable(Exists::class.java.classLoader))


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type_id)
        parcel.writeString(icon)
        parcel.writeString(name)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeByte(if (isDefault) 1 else 0)
        parcel.writeString(accountNumber)
        parcel.writeInt(id)
        parcel.writeParcelable(exists, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentModesModal> {
        override fun createFromParcel(parcel: Parcel): PaymentModesModal {
            return PaymentModesModal(parcel)
        }

        override fun newArray(size: Int): Array<PaymentModesModal?> {
            return arrayOfNulls(size)
        }
    }
}