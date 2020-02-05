package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hemant on 23/1/17.
 */
public class CityInfoItem implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("cityName")
    private String cityName;
    @SerializedName("lon")
    private double lon;
    @SerializedName("lat")
    private double lat;
    @SerializedName("isSelected")
    private boolean isSelected;

    protected CityInfoItem(Parcel in) {
        id = in.readString();
        cityName = in.readString();
        lon = in.readDouble();
        lat = in.readDouble();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<CityInfoItem> CREATOR = new Creator<CityInfoItem>() {
        @Override
        public CityInfoItem createFromParcel(Parcel in) {
            return new CityInfoItem(in);
        }

        @Override
        public CityInfoItem[] newArray(int size) {
            return new CityInfoItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cityName);
        dest.writeDouble(lon);
        dest.writeDouble(lat);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
