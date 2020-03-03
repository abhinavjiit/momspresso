package com.mycity4kids.models.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ShortShortiesConfig(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("category_image_url")
    var category_image_url: String? = null,
    @SerializedName("is_active")
    var is_active: Boolean,
    @SerializedName("created_by")
    var created_by: String? = null,
    @SerializedName("short_story_id")
    var short_story_id: String? = null,
    @SerializedName("font_size_title")
    var font_size_title: Int = 0,
    @SerializedName("font_size_body")
    var font_size_body: Int = 0,
    @SerializedName("font_alignment")
    var font_alignment: String? = null,
    @SerializedName("font_colour")
    var font_colour: String? = null,
    @SerializedName("coordinate_x")
    var coordinate_x: Float = 0.toFloat(),
    @SerializedName("coordinate_y")
    var coordinate_y: Float = 0.toFloat(),
    @SerializedName("user_id")
    var user_id: String? = null,
    @SerializedName("category_image")
    var category_image: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(category_image_url)
        parcel.writeByte(if (is_active) 1 else 0)
        parcel.writeString(created_by)
        parcel.writeString(short_story_id)
        parcel.writeInt(font_size_title)
        parcel.writeInt(font_size_body)
        parcel.writeString(font_alignment)
        parcel.writeString(font_colour)
        parcel.writeFloat(coordinate_x)
        parcel.writeFloat(coordinate_y)
        parcel.writeString(user_id)
        parcel.writeInt(category_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShortShortiesConfig> {
        override fun createFromParcel(parcel: Parcel): ShortShortiesConfig {
            return ShortShortiesConfig(parcel)
        }

        override fun newArray(size: Int): Array<ShortShortiesConfig?> {
            return arrayOfNulls(size)
        }
    }
}
