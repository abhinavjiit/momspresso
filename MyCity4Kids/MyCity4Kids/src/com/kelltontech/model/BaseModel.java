package com.kelltontech.model;


import android.os.Parcel;
import android.os.Parcelable;


public class BaseModel implements Parcelable {

	private int dbTableRowId;
	

	/**
	 * @return the dbTableRowId
	 */
	public int getDbTableRowId() {
		return dbTableRowId;
	}

	/**
	 * @param dbTableRowId the dbTableRowId to set
	 */
	public void setDbTableRowId(int dbTableRowId) {
		this.dbTableRowId = dbTableRowId;
	}
	
	/**
	 * no-arg constructor
	 */
	public BaseModel()
	{
		
	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public BaseModel(Parcel source)
	{
		dbTableRowId = source.readInt();
		
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(dbTableRowId);
		
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
