package com.mycity4kids.models.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class AgeGroup extends BaseDataModel{
	private String key;
	private String value;
	private boolean selected ; 


	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
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

	public AgeGroup() {
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
		dest.writeByte((byte) (selected ? 1 : 0));   



	}

	public AgeGroup(Parcel in)
	{
		this.key = in.readString();
		this.value = in.readString();
		this.selected = in.readByte() != 0;
	}

	public static final Parcelable.Creator<AgeGroup>	CREATOR	= new Parcelable.Creator<AgeGroup>()
			{

		public AgeGroup createFromParcel(Parcel in)
		{
			return new AgeGroup(in);
		}

		public AgeGroup[] newArray(int size)
		{
			return new AgeGroup[size];
		}
			};


}
