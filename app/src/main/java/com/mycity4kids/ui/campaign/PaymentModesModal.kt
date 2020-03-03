package com.mycity4kids.ui.campaign

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentModesModal(
    @SerializedName("type_id")
    @Expose
    var type_id: Int = 0,
    @SerializedName("icon")
    @Expose
    var icon: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("isChecked")
    @Expose
    var isChecked: Boolean = false,
    @SerializedName("isDefault")
    @Expose
    var isDefault: Boolean = false,
    @SerializedName("accountNumber")
    @Expose
    var accountNumber: String? = null,
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("exists")
    @Expose
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
