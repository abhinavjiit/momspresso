package com.mycity4kids.profile

import android.os.Parcel
import android.os.Parcelable


data class Crown(
        val bg_url: String,
        val crown: CrownX,
        val desc: String,
        val image_url: String,
        val language: String,
        val rank: Int,
        val sharing_url: String,
        val title: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(CrownX::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bg_url)
        parcel.writeParcelable(crown, flags)
        parcel.writeString(desc)
        parcel.writeString(image_url)
        parcel.writeString(language)
        parcel.writeInt(rank)
        parcel.writeString(sharing_url)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Crown> {
        override fun createFromParcel(parcel: Parcel): Crown {
            return Crown(parcel)
        }

        override fun newArray(size: Int): Array<Crown?> {
            return arrayOfNulls(size)
        }
    }
}

data class CrownX(
        val image: String,
        val name: String,
        val weightage: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeInt(weightage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CrownX> {
        override fun createFromParcel(parcel: Parcel): CrownX {
            return CrownX(parcel)
        }

        override fun newArray(size: Int): Array<CrownX?> {
            return arrayOfNulls(size)
        }
    }
}