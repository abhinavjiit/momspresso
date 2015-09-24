package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class Timings implements Parcelable{
	private String start_time;
	private String end_time;
	
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	/**
	 * Default constructor
	 */
	public Timings() {
		// nothing to do here
	}

	/**
	 * @param in
	 *            , to create object from Parcel
	 */
	public Timings(Parcel in) {
		this.start_time = in.readString();
		this.end_time = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(start_time);
		dest.writeString(end_time);
	}

	/**
	 * to create list of objects to/from parcel
	 */
	public static final Parcelable.Creator<Timings> CREATOR = new Parcelable.Creator<Timings>() {

		public Timings createFromParcel(Parcel in) {
			return new Timings(in);
		}

		public Timings[] newArray(int size) {
			return new Timings[size];
		}
	};

	@Override
	public int describeContents() {
		// nothing to do here
		return 0;
	}

}
