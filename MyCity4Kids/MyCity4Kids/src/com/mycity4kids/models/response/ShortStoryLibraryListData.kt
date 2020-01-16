package com.mycity4kids.models.response

import android.os.Parcel
import android.os.Parcelable


class ShortStoryLibraryListData(var id: Int? = 0,
                                var name: String? = null,
                                var image_url: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(),
            parcel.readString(),
            parcel.readString())


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        id?.let { dest.writeInt(it) }
        dest.writeString(name)
        dest.writeString(image_url)
    }
    companion object CREATOR : Parcelable.Creator<ShortStoryLibraryListData> {
        override fun createFromParcel(parcel: Parcel): ShortStoryLibraryListData {
            return ShortStoryLibraryListData(parcel)
        }

        override fun newArray(size: Int): Array<ShortStoryLibraryListData?> {
            return arrayOfNulls(size)
        }
    }
}
