package com.mycity4kids.models.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class SortBy extends BaseDataModel{
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	
	public SortBy(){
		
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public SortBy(Parcel source)
	{
		
		this.key=source.readString();
		this.value=source.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	dest.writeString(key);
	dest.writeString(value);
		
	}
	
	public static Parcelable.Creator<SortBy>	CREATOR	= new Parcelable.Creator<SortBy>() {
		public SortBy createFromParcel(Parcel source) {
			return new SortBy(source);
		}

		public SortBy[] newArray(int size) {
			return new SortBy[size];
		}
	};
}
