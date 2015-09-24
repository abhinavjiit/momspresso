package com.mycity4kids.googlemap.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Deepanker Chaudhary
 *
 */
public class Location implements Parcelable{
	private double lat;
	private double lng;

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	/**
	 * @return the lng
	 */
	public double getLng() {
		return lng;
	}
	/**
	 * @param lng the lng to set
	 */
	public void setLng(double lng) {
		this.lng = lng;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public Location() {
		// TODO Auto-generated constructor stub
	}
	public Location(Parcel source){
		  readFromParcel(source);
		 }
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		
	}
	public void readFromParcel(Parcel source){
		  lat = source.readDouble();
		  lng = source.readDouble();
		 }
	 public static final Parcelable.Creator<Location> CREATOR =
			   new Parcelable.Creator<Location>(){

			    @Override
			    public Location createFromParcel(Parcel source) {
			     return new Location(source);
			    }

			    @Override
			    public Location[] newArray(int size) {
			     return new Location[size];
			    }
			 };
			 
}
