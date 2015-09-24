package com.mycity4kids.models.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class AdvancedSearch extends BaseDataModel{
	private String key;
	private String value;
	
	public String getKey() {
		return key;
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
	public AdvancedSearch() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(key);
		dest.writeString(value);
	
	

	}

	public AdvancedSearch(Parcel in)
	{
		this.key = in.readString();
		this.value = in.readString();
		
	}
	
	public static final Parcelable.Creator<AdvancedSearch>	CREATOR	= new Parcelable.Creator<AdvancedSearch>()
			{

		public AdvancedSearch createFromParcel(Parcel in)
		{
			return new AdvancedSearch(in);
		}

		public AdvancedSearch[] newArray(int size)
		{
			return new AdvancedSearch[size];
		}
			};
	
}
