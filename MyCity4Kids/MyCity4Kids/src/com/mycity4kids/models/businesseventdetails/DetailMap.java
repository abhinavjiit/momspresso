package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailMap implements Parcelable{
	private String latitude;
	private String longitude;
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(latitude);
		dest.writeString(longitude);


	}

	public DetailMap(Parcel in)
	{
		this.latitude=in.readString();
		this.longitude=in.readString();


	}
	public DetailMap() {
		// TODO Auto-generated constructor stub
	}
	public static final Parcelable.Creator<DetailMap>	CREATOR	= new Parcelable.Creator<DetailMap>()
			{

		public DetailMap createFromParcel(Parcel in)
		{
			return new DetailMap(in);
		}

		public DetailMap[] newArray(int size)
		{
			return new DetailMap[size];
		}
			};
}
