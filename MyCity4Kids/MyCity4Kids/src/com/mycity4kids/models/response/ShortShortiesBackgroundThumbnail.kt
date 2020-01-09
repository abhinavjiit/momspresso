package com.mycity4kids.models.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ShortShortiesBackgroundThumbnail(
        @SerializedName("images")
        @Expose
        var images: ImageListData,
        @SerializedName("categories")
        @Expose
        var categories: ArrayList<Categories>? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(ImageListData::class.java.classLoader),
            parcel.createTypedArrayList(Categories.CREATOR))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(images, flags)
        parcel.writeTypedList(categories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShortShortiesBackgroundThumbnail> {
        override fun createFromParcel(parcel: Parcel): ShortShortiesBackgroundThumbnail {
            return ShortShortiesBackgroundThumbnail(parcel)
        }

        override fun newArray(size: Int): Array<ShortShortiesBackgroundThumbnail?> {
            return arrayOfNulls(size)
        }
    }
}

class ImageListData(@SerializedName("next")
                    var next: String? = null,
                    @SerializedName("previous")
                    var previous: String? = null,
                    @SerializedName("results")
                    var results: ArrayList<Images>? = null) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Images.CREATOR)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(next)
        parcel.writeString(previous)
        parcel.writeTypedList(results)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageListData> {
        override fun createFromParcel(parcel: Parcel): ImageListData {
            return ImageListData(parcel)
        }

        override fun newArray(size: Int): Array<ImageListData?> {
            return arrayOfNulls(size)
        }
    }

}

class Images(
        @SerializedName("image_url")
        var image_url: String? = null,
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("font_colour")
        var font_colour: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image_url)
        id?.let { parcel.writeInt(it) }
        parcel.writeString(font_colour)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Images> {
        override fun createFromParcel(parcel: Parcel): Images {
            return Images(parcel)
        }

        override fun newArray(size: Int): Array<Images?> {
            return arrayOfNulls(size)
        }
    }

}


class Categories(
        @SerializedName("category_id")
        var category_id: String? = null,
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("image_url")
        var image_url: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category_id)
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(image_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Categories> {
        override fun createFromParcel(parcel: Parcel): Categories {
            return Categories(parcel)
        }

        override fun newArray(size: Int): Array<Categories?> {
            return arrayOfNulls(size)
        }
    }
}