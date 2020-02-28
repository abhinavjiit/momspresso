package com.mycity4kids.profile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Crown(
    @SerializedName("bg_url")
    @Expose
    val bg_url: String?,
    @SerializedName("crown")
    @Expose
    val crown: CrownX?,
    @SerializedName("desc")
    @Expose
    val desc: String?,
    @SerializedName("image_url")
    @Expose
    val image_url: String?,
    @SerializedName("language")
    @Expose
    val language: String?,
    @SerializedName("rank")
    @Expose
    val rank: Int,
    @SerializedName("sharing_url")
    @Expose
    val sharing_url: String?,
    @SerializedName("title")
    @Expose
    val title: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(CrownX::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
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
    val image: String?,
    val name: String?,
    val weightage: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
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
