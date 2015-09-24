package com.mycity4kids.models.basemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;




public class BaseDataModel extends BaseModel{
	private int categoryId;

	
	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	
	
	/**
	 * no-arg constructor
	 */
	public BaseDataModel()
	{
		
	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public BaseDataModel(Parcel source)
	{
		
		categoryId=source.readInt();
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	
		dest.writeInt(categoryId);
	}
	
	public static Parcelable.Creator<BaseModel>	CREATOR	= new Parcelable.Creator<BaseModel>() {
		public BaseModel createFromParcel(Parcel source) {
			return new BaseModel(source);
		}

		public BaseModel[] newArray(int size) {
			return new BaseModel[size];
		}
	};
	
}
