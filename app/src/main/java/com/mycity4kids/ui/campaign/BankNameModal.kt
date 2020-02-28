package com.mycity4kids.ui.campaign

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BankNameModal(
    @SerializedName("id")
    @Expose
    val id: Int = -1,
    @SerializedName("name")
    @Expose
    val name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BankNameModal> {
        override fun createFromParcel(parcel: Parcel): BankNameModal {
            return BankNameModal(parcel)
        }

        override fun newArray(size: Int): Array<BankNameModal?> {
            return arrayOfNulls(size)
        }
    }
}
