package com.mycity4kids.models.parentingfilter;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterTags implements Parcelable{
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
	
	public FilterTags()
	{

	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public FilterTags(Parcel source)
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

	public static Parcelable.Creator<FilterTags>	CREATOR	= new Parcelable.Creator<FilterTags>() {
		public FilterTags createFromParcel(Parcel source) {
			return new FilterTags(source);
		}

		public FilterTags[] newArray(int size) {
			return new FilterTags[size];
		}
	};
}
