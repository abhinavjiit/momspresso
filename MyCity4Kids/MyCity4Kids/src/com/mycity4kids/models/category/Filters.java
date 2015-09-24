package com.mycity4kids.models.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class Filters extends BaseDataModel{
	private int id;
	private String name;
	private boolean isSelected;
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public int getKey() {
		return id;
	}
	public void setKey(int key) {
		this.id = key;
	}
	public String getValue() {
		return name;
	}
	public void setValue(String value) {
		this.name = value;
	}
	public Filters() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(id);
		dest.writeString(name);
	
	

	}

	public Filters(Parcel in)
	{
		this.id = in.readInt();
		this.name = in.readString();
		
		
	}
	
	public static final Parcelable.Creator<Filters>	CREATOR	= new Parcelable.Creator<Filters>()
			{

		public Filters createFromParcel(Parcel in)
		{
			return new Filters(in);
		}

		public Filters[] newArray(int size)
		{
			return new Filters[size];
		}
			};

}
