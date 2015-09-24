package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class EventDate implements Parcelable{
	private String start_date;
	private String end_date;


	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	/**
	 * Default constructor
	 */
	public EventDate() {
		// nothing to do here
	}

	/**
	 * @param in
	 *            , to create object from Parcel
	 */
	public EventDate(Parcel in) {
		this.start_date = in.readString();
		this.end_date = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(start_date);
		dest.writeString(end_date);
	}

	/**
	 * to create list of objects to/from parcel
	 */
	public static final Parcelable.Creator<EventDate> CREATOR = new Parcelable.Creator<EventDate>() {

		public EventDate createFromParcel(Parcel in) {
			return new EventDate(in);
		}

		public EventDate[] newArray(int size) {
			return new EventDate[size];
		}
	};

	@Override
	public int describeContents() {
		// nothing to do here
		return 0;
	}

}
