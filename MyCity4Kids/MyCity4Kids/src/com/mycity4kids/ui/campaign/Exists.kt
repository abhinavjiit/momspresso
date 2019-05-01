package com.mycity4kids.ui.campaign

import android.os.Parcel
import android.os.Parcelable

data class Exists(var account_number: String,
                  var id: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_number)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exists> {
        override fun createFromParcel(parcel: Parcel): Exists {
            return Exists(parcel)
        }

        override fun newArray(size: Int): Array<Exists?> {
            return arrayOfNulls(size)
        }
    }
}

