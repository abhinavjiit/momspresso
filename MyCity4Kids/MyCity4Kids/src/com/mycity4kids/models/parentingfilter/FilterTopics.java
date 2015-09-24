package com.mycity4kids.models.parentingfilter;

import android.os.Parcel;
import android.os.Parcelable;


public class FilterTopics implements Parcelable{
	private String id;
	private String name;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public FilterTopics()
	{

	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public FilterTopics(Parcel source)
	{
		this.id = source.readString();
		this.name=source.readString();
		

	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(id);
		dest.writeString(name);


	}

	public static Parcelable.Creator<FilterTopics>	CREATOR	= new Parcelable.Creator<FilterTopics>() {
		public FilterTopics createFromParcel(Parcel source) {
			return new FilterTopics(source);
		}

		public FilterTopics[] newArray(int size) {
			return new FilterTopics[size];
		}
	};
}
